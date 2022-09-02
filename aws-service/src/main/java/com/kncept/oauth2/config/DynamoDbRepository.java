package com.kncept.oauth2.config;

import com.kncept.oauth2.config.annotation.OidcIdField;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.lang.reflect.*;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class DynamoDbRepository<T> {
    private final Class<T> valueInterface;
    private final DynamoDbClient client;
    private final String tableName;

    private String idFieldName;

    public DynamoDbRepository(Class<T> valueInterface, String tableName) {
        this(valueInterface, DynamoDbClient.create(), tableName);
    }

    public DynamoDbRepository(
            Class<T> valueInterface,
            DynamoDbClient client,
            String tableName
    ) {
        this.valueInterface = valueInterface;
        this.client = client;
        this.tableName = tableName;
    }

    private String idFieldName() {
        if (idFieldName == null) {
            for (Method m : valueInterface.getDeclaredMethods()) {
                if (m.getAnnotation(OidcIdField.class) != null) {
                    idFieldName = m.getName();
                }
            }
            throw new IllegalStateException("OIDC Type without an ID field");
        }
        return idFieldName;
    }

    // https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb/CreateTable.java
    public synchronized void createTableIfNotExists() {
        DescribeTableRequest describeTable = DescribeTableRequest.builder()
                .tableName(tableName)
                .build();
        boolean exists = false;
        try {
            DescribeTableResponse response = client.describeTable(describeTable);
            exists = response != null;
        } catch (ResourceNotFoundException rnf) {
        }
        if (!exists) {
            DynamoDbWaiter waiter = client.waiter();
            client.createTable(CreateTableRequest.builder()
                    .tableName(tableName)
                    .keySchema(KeySchemaElement.builder()
                            .attributeName(idFieldName())
                            .keyType(KeyType.HASH)
                            .build())
                    .build());
            waiter.waitUntilTableExists(describeTable);
        }
    }


    // there has to be an easier (and better) way than this.
    T reflectiveItemConverter(Map<String, AttributeValue> item) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) classLoader = getClass().getClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, new Class[] {valueInterface}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                AttributeValue av = item.get(methodName);
                return fromAttributeValue(av, method);
            }
        });
    }
    
    public Object fromAttributeValue(AttributeValue av, Method typeDetails) throws ClassNotFoundException {
    	Class<?> type = typeDetails.getReturnType();
    	// unroll optional
    	if (Optional.class.isAssignableFrom(type)) {
    		try {
    			if (av.nul()) return Optional.empty();	
    		} catch (NullPointerException e) {
    			// means it's not null... ugh
    		}
    		
    		ParameterizedType genericReturnType = (ParameterizedType)typeDetails.getGenericReturnType();
    		Type[] typeArgs = genericReturnType.getActualTypeArguments();
    		String optionalGenericTypeParamName = typeArgs[0].getTypeName(); // eg: "java.lang.String" ... ugh
    		return Optional.of(fromAttributeValue(av, Class.forName(optionalGenericTypeParamName)));
    		
    	}
    	return fromAttributeValue(av, type);
    }
    
    public Object fromAttributeValue(AttributeValue av, Class<?> type) throws ClassNotFoundException {
    	try {
			if (av.nul()) return null;
		} catch (NullPointerException e) {
			// means it's not null... ugh
		}
    	if (String.class.isAssignableFrom(type)) return av.s();
    	if (Boolean.class.isAssignableFrom(type)) return av.bool();
    	if (boolean.class.isAssignableFrom(type)) return av.bool();
    	
    	throw new RuntimeException("Unable to deconvert field of type " + type.getName());
    }

    public AttributeValue toAttributeValue(Object value) {
    	if (value == null) return AttributeValue.builder().nul(true).build();
    	if (value instanceof Optional) {
    		if (((Optional) value).isEmpty()) return AttributeValue.builder().nul(true).build();
    		value = ((Optional) value).get();
    	}
    	if (value instanceof String) return AttributeValue.builder().s((String)value).build();
    	if (value instanceof Boolean) return AttributeValue.builder().bool((Boolean)value).build();
    	
    	throw new RuntimeException("Unable to convert value of type" + value.getClass().getSimpleName());
    }
    public Map<String, AttributeValue> convert(String key, T value) {
    	try {
	        Map<String, AttributeValue> ddbValue = new TreeMap<>();
	        for(Method m: valueInterface.getDeclaredMethods()) {
	        	Object fieldValue = m.invoke(value);
	        	ddbValue.put(m.getName(), toAttributeValue(fieldValue));
	        }
	        return ddbValue;
    	} catch (IllegalAccessException e) {
    		throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			 throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} 
    }

    public void write(String key, T value) {
        client.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(convert(key, value))
                .build());
    }

    public void delete(String key)  {
        client.deleteItem(DeleteItemRequest.builder()
                .key(Map.of(idFieldName(), AttributeValue.fromS(key)))
                .build());
    }

    public T findById(String key) {
        try {
            GetItemResponse response = client.getItem(GetItemRequest.builder()
                    .tableName(tableName)
                    .key(Map.of(idFieldName(), AttributeValue.fromS(key)))
                    .build());
            return reflectiveItemConverter(response.item());
        } catch (ResourceNotFoundException rnf) {
            return null;
        }
    }



}
