package com.kncept.oauth2;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

public class HandlerTest {


    @Test
    public void canInvokeHandler() {
        Handler handler = new Handler();

        APIGatewayProxyResponseEvent result = handler.handleRequest(createSyntheticEvent(), createSyntheticContext());

        Assertions.assertEquals(200, result.getStatusCode());
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

    // because a publicly accessible builder so people can test locally would be too convinient
    private APIGatewayProxyRequestEvent createSyntheticEvent() {
        return new APIGatewayProxyRequestEvent();

    }

}
