package com.kncept.oauth2;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.kncept.oauth2.config.InMemoryConfiguration;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HandlerTest {


    @Test
    public void canInvokeHandler() {
        Handler handler = new Handler(new InMemoryConfiguration());
        APIGatewayProxyRequestEvent event = createSyntheticEvent();
        event.setPath("/notfound");
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, createSyntheticContext());
//        assertEquals("", result.getBody()); // we get an error page on 404's now. //TODO: Introduce a no content response type
        assertEquals(404, result.getStatusCode());
    }

    @Test
    public void canInvokeCssHandler() {
        Handler handler = new Handler(new InMemoryConfiguration());
        APIGatewayProxyRequestEvent event = createSyntheticEvent();
        event.setPath("/style.css");
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, createSyntheticContext());
        assertEquals(200, result.getStatusCode());
    }

    @Test
    public void canSplitParams() throws Exception {
        Handler handler = new Handler(new InMemoryConfiguration());
        String s = "grant_type=authorization_code\n" +
                "&client_id=kncept-oidc-client\n" +
                "&client_secret=none\n" +
                "&redirect_uri=https://openidconnect.net/callback\n" +
                "&code=8381357e-e02e-4b46-b73e-b2a24f629027";
        Map<String, String> params = handler.extractSimpleParams(s.replaceAll("\n", ""));
        assertEquals("kncept-oidc-client", params.get("client_id"));

        params = handler.extractSimpleParams(s);
        assertEquals("kncept-oidc-client", params.get("client_id").trim());

    }

    private Context createSyntheticContext() {
        return (Context)Proxy.newProxyInstance(Context.class.getClassLoader(), new Class[]{Context.class}, (proxy, method, args) -> {
            if (method.getReturnType().equals(String.class)) return method.getName();
            if (method.getReturnType().equals(int.class)) return 0;
            if (method.getName().equals("getLogger")) return new LambdaLogger(){
                @Override
                public void log(String message) {
                    System.out.println(message);
                }
                @Override
                public void log(byte[] message) {
                    System.out.println(new String(message));
                }
            };
            throw new RuntimeException("Unknown method invoked on AWS Lambda Context: " + method.getName());
        });
    }

    private APIGatewayProxyRequestEvent createSyntheticEvent() {
        APIGatewayProxyRequestEvent event =  new APIGatewayProxyRequestEvent();
        event.setHttpMethod("GET");
        event.setPath("");
        return event;
    }

}
