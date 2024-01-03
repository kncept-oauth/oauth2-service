package com.kncept.oauth2.config;

import com.kncept.oauth2.entity.EntityId;
import com.kncept.oauth2.entity.IdentifiedEntity;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class DynamoDbRepository implements SingleStorageConfiguration.CrudRepo {
//    private final Class<T> valueInterface;
    public final DynamoDbClient client;
    public final String tableName;

    private final Map<String, Class> typeRegistrations;

    public DynamoDbRepository(String tableName) {
        this(DynamoDbClient.create(), tableName);
    }

    public DynamoDbRepository(
            DynamoDbClient client,
            String tableName
    ) {
        this.client = client;
        this.tableName = tableName;

        typeRegistrations = new HashMap<>();
    }

    @Override
    public <T extends IdentifiedEntity> void registerEntityType(String entityType, Class<T> javaType) {
        typeRegistrations.put(entityType, javaType);
    }

    private <T extends IdentifiedEntity> void write(T entity) {
        client.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(convert(entity))
                .build());
    }

    @Override
    public <T extends IdentifiedEntity> void create(T entity) {
        write(entity);
    }

    @Override
    public <T extends IdentifiedEntity> T read(EntityId id) {
        try {
            GetItemResponse response = client.getItem(GetItemRequest.builder()
                    .tableName(tableName)
                    .key(Map.of("id", AttributeValue.fromS(id.toString())))
                    .build());
            return reflectiveItemConverter(response.item());
        } catch (ResourceNotFoundException rnf) {
            return null;
        }
    }

    @Override
    public <T extends IdentifiedEntity> List<T> list(String... entityTypes) {
        // TODO: don't tablescan and filter... push the filter up.
        ScanResponse scanResponse = client.scan(ScanRequest.builder().tableName(tableName).build());
        List<?> unfiltered = scanResponse.items().stream().map(this::reflectiveItemConverter).collect(Collectors.toList());
        return (List<T>) unfiltered.stream().filter(v -> ((IdentifiedEntity)v).getId().isOfType(entityTypes)).collect(Collectors.toList());
    }

    @Override
    public <T extends IdentifiedEntity> void update(T entity) {
        write(entity);
    }

    @Override
    public <T extends IdentifiedEntity> void delete(T entity) {
        client.deleteItem(DeleteItemRequest.builder()
                .key(Map.of("id", AttributeValue.fromS(entity.getId().toString())))
                .build());
    }

    //    public long epochSecondsExpiry(long secondsDuration) {
//        long epochSecond = System.currentTimeMillis() / 1000L;
//        return epochSecond + secondsDuration;
//    }

    private List<Field> fields(Class<?> javaType) {
        List<Field> fields = new ArrayList<>();
        for(Field field: javaType.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (Modifier.isTransient(field.getModifiers())) continue;
            fields.add(field);
        }
        return fields;
    }
    private Object get(Field field, Object obj) throws IllegalAccessException {
        if (!field.isAccessible()) field.setAccessible(true);
        return field.get(obj);
    }

    private void set(Field field, Object obj, Object value) throws IllegalAccessException {
        if (!field.isAccessible()) field.setAccessible(true);
        field.set(obj, value);
    }

    // https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb/CreateTable.java
    public synchronized void createTableIfNotExists() {
        TableStatus tableStatus = null;
        DescribeTableRequest describeTable = DescribeTableRequest.builder()
                .tableName(tableName)
                .build();
        try {
            DescribeTableResponse describeTableResponse = client.describeTable(describeTable);
            tableStatus = describeTableResponse.table().tableStatus();
        } catch (ResourceNotFoundException rnf) { }
        if (tableStatus == null || !(tableStatus.equals(TableStatus.ACTIVE) || tableStatus.equals(TableStatus.CREATING))) {
            System.out.println(getClass().getSimpleName() + " creating table " + tableName);
            client.createTable(CreateTableRequest.builder()
                    .tableName(tableName)
                    .keySchema(KeySchemaElement.builder()
                            .attributeName("id")
                            .keyType(KeyType.HASH)
                            .build())
                    .attributeDefinitions(
                            new AttributeDefinition[] {AttributeDefinition.builder()
                                    .attributeName("id")
                                    .attributeType(ScalarAttributeType.S)
                                    .build()}
                    )
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build());
        }
        while (tableStatus == null || !tableStatus.equals(TableStatus.ACTIVE)) {
            try { Thread.sleep(5); }
            catch (InterruptedException e) { }
            try {
                DescribeTableResponse describeTableResponse = client.describeTable(describeTable);
                tableStatus = describeTableResponse.table().tableStatus();
            } catch (ResourceNotFoundException rnf) { }
        }


        DescribeTimeToLiveResponse ttlResponse = client.describeTimeToLive(DescribeTimeToLiveRequest.builder()
                .tableName(tableName)
                .build());
        TimeToLiveStatus ttlStatus = ttlResponse.timeToLiveDescription().timeToLiveStatus();
        if (ttlStatus == null || !(ttlStatus.equals(TimeToLiveStatus.ENABLED) || ttlStatus.equals(TimeToLiveStatus.ENABLING))) {
            System.out.println(getClass().getSimpleName() + " updating ttl expiry " + tableName);
            client.updateTimeToLive(UpdateTimeToLiveRequest.builder()
                    .tableName(tableName)
                    .timeToLiveSpecification(TimeToLiveSpecification.builder()
                            .attributeName("expiry")
                            .enabled(true)
                            .build())
                    .build());
        }
    }


    // read data from ddb
    <T extends IdentifiedEntity> T reflectiveItemConverter(Map<String, AttributeValue> item) {
        if (item == null || item.isEmpty()) return null;

        System.out.println(item);
        EntityId id = EntityId.parse(item.get("id").s());
        Class<?> javaType = typeRegistrations.get(id.type);
        if (javaType == null) throw new IllegalStateException("Unknown type: " + id.type);

        try {
            T value = (T) javaType.getDeclaredConstructor().newInstance();
            for (Field field : fields(javaType)) {
                AttributeValue av = item.get(field.getName());
                Object fieldValue = fromAttributeValue(av, field.getType());
                set(field, value, fieldValue);
            }
            return value;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public Object fromAttributeValue(AttributeValue av, Class<?> type) {
    	try {
			if (av == null || av.type() == AttributeValue.Type.NUL || av.nul()) {
                if (Optional.class.isAssignableFrom(type)) return Optional.empty();
                return null;
            }
		} catch (NullPointerException e) {
			// means it's not null... ugh
		}
        System.out.println("at type " + av.type());
        // S
        // BOOL

        if (Optional.class.isAssignableFrom(type)) {
            TypeVariable[] tv = type.getTypeParameters();
            System.out.println(tv[0].getTypeName() + "  " + tv[0]);

            if (av.type() == AttributeValue.Type.S) return Optional.of(av.s());
            if (av.type() == AttributeValue.Type.BOOL) return Optional.of(av.bool());
        }

    	if (String.class.isAssignableFrom(type)) return av.s();
    	if (Boolean.class.isAssignableFrom(type)) return av.bool();
    	if (boolean.class.isAssignableFrom(type)) return av.bool();
        if (Long.class.isAssignableFrom(type)) return Long.valueOf(av.n());
        if (boolean.class.isAssignableFrom(type)) return Long.valueOf(av.n());
        if (EntityId.class.isAssignableFrom(type)) return EntityId.parse(av.s());
        if (LocalDateTime.class.isAssignableFrom(type)) return LocalDateTime.ofEpochSecond(Long.valueOf(av.n()), 0, ZoneOffset.UTC);

    	throw new RuntimeException("Unable to deconvert field of type " + type.getName());
    }

    // write data to ddb
    // n.b. primitive types will be autoboxed
    public AttributeValue toAttributeValue(Object value) {
    	if (value == null) return AttributeValue.builder().nul(true).build();
    	if (value instanceof Optional) {
    		if (((Optional) value).isEmpty()) return AttributeValue.builder().nul(true).build();
    		value = ((Optional) value).get();
    	}
    	if (value instanceof String) return AttributeValue.fromS((String)value);
    	if (value instanceof Boolean) return AttributeValue.fromBool((Boolean)value);
        if (value instanceof Long) return AttributeValue.fromN(value.toString());
        if (value instanceof EntityId) return AttributeValue.fromS(value.toString());
        if (value instanceof LocalDateTime) return AttributeValue.fromN(Long.toString(((LocalDateTime)value).toEpochSecond(ZoneOffset.UTC)));
    	throw new RuntimeException("Unable to convert value of type" + value.getClass().getSimpleName());
    }
    public <T extends IdentifiedEntity> Map<String, AttributeValue> convert(T value) {
    	try {
            Map<String, AttributeValue> ddbValues = new TreeMap<>();
            Class<?> javaType = value.getClass();
            for(Field field: fields(javaType)) {
                String fieldName = field.getName();
                // or do I need to do the getter (or is) here?
                Object fieldValue = get(field, value);
                ddbValues.put(fieldName, toAttributeValue(fieldValue));
            }
	        return ddbValues;
    	} catch (IllegalAccessException e) {
    		throw new RuntimeException(e);
		}
    }

}
