package com.kncept.oauth2.config;

import com.kncept.oauth2.config.authrequest.AuthRequest;
import com.kncept.oauth2.config.client.Client;
import com.kncept.oauth2.config.user.User;

import com.kncept.oauth2.entity.EntityId;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DynamoDbRepositoryTest {

    @Test
    public void canConvertClient() {
        DynamoDbRepository repository = new DynamoDbRepository(null, "SimpleOidc");
        repository.registerEntityType(Client.EntityType, "*", Client.class);


        String clientId = UUID.randomUUID().toString();

        Client original = new Client() ;
        original.setId(Client.id(clientId));
        original.setEnabled(true);
        Map<String, AttributeValue> converted = repository.convert(original);
        assertNotNull(converted);
        
        assertTrue(converted.containsKey("id"));
        assertEquals(Client.EntityType + "/" + clientId, converted.get("id").s());
        assertTrue(converted.containsKey("enabled"));
        assertEquals(original.isEnabled(), converted.get("enabled").bool());

        assertFalse(converted.containsKey("absent"));
        
        Client reconstitued = repository.reflectiveItemConverter(converted);
        assertEquals(original.getId(), reconstitued.getId());
        assertEquals(original.isEnabled(), reconstitued.isEnabled());
    }
    
    @Test
    public void canConvertAuthRequest() {
        DynamoDbRepository repository = new DynamoDbRepository(null, "SimpleOidc");
        repository.registerEntityType(AuthRequest.EntityType, "*", AuthRequest.class);
//        String oauthSessionId = UUID.randomUUID().toString();
        AuthRequest original = new AuthRequest();
        original.setId(AuthRequest.id("oauthSessionId string"));
        original.setState(Optional.of("state string"));
        original.setNonce(Optional.empty());
        original.setRedirectUri("redirectUri string");
        original.setRef(Client.id("clientId string"));
        original.setResponseType("responseType string");
        original.setExpiry(null);
        Map<String, AttributeValue> converted = repository.convert(original);
        AuthRequest reconstitued = (AuthRequest)repository.reflectiveItemConverter(converted);
        assertEquals(original.getId(), reconstitued.getId());
        assertEquals(original.getState(), reconstitued.getState());
        assertEquals(original.getNonce(), reconstitued.getNonce());
        assertEquals(original.getRedirectUri(), reconstitued.getRedirectUri());
        assertEquals(original.getRef(), reconstitued.getRef());
        assertEquals(original.getResponseType(), reconstitued.getResponseType());
    }
    
    @Test
    public void typeConversions() {
        DynamoDbRepository repository = new DynamoDbRepository(null, "SimpleOidc");
    	AttributeValue av = null;
    	
    	av = repository.toAttributeValue("stringValue");
    	assertEquals("stringValue", av.s());
    	
    	av = repository.toAttributeValue(true);
    	assertEquals(true, av.bool());
    	
    	av = repository.toAttributeValue(null);
    	assertEquals(true, av.nul());
    	
    	av = repository.toAttributeValue(Optional.empty());
    	assertEquals(true, av.nul());
    	
    	av = repository.toAttributeValue(Optional.of("stringOption"));
    	assertEquals("stringOption", av.s());
    }
    
    @Test
    public void typeDeconversions() throws Exception {
        DynamoDbRepository repository = new DynamoDbRepository(null, "SimpleOidc");
    	AttributeValue av = null;
    	Object value = null;
    	
    	av = AttributeValue.fromS("stringValue");
    	value = repository.fromAttributeValue(av, String.class);
    	assertEquals("stringValue", value);

        av = AttributeValue.fromBool(true);
        value = repository.fromAttributeValue(av, Boolean.class);
        assertEquals(true, value);

//        av = AttributeValue.fromBool(true);
//        value = repository.fromAttributeValue(av, String.class);
//        assertEquals(true, value);

    	av = AttributeValue.fromNul(true);
    	value = repository.fromAttributeValue(av, String.class);
    	assertEquals(null, value);


        Optional<String> stringOptional = Optional.empty();
    	av = AttributeValue.fromNul(true);
    	value = repository.fromAttributeValue(av, stringOptional.getClass()); // Optional<String>
    	assertEquals(Optional.empty(), value);
    	
    	av = AttributeValue.fromS("stringOption");
    	value = repository.fromAttributeValue(av, stringOptional.getClass()); // method returning Optional<String>
    	assertEquals(Optional.of("stringOption"), value);
    }

    @Test
    public void canConvertUser() {
        DynamoDbRepository repository = new DynamoDbRepository(null, "SimpleOidc");
        repository.registerEntityType(User.EntityType, "*", User.class);

        EntityId randomUserId = User.id();
        User original = new User();
        original.setId(randomUserId);
        original.setUsername("simpleusername");
        original.setSalt(" salt");
        original.setPassword("precomputedHash");
        original.setWhen(LocalDateTime.now(Clock.systemUTC()));
        Map<String, AttributeValue> converted = repository.convert(original);
        User reconstitued = repository.reflectiveItemConverter(converted);
        assertEquals(randomUserId, reconstitued.getId());
        assertEquals(" salt", reconstitued.getSalt());
    }
}
