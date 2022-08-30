package com.kncept.oauth2;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

public class HandlerTest {


    @Test
    public void canInvokeHandler() {
        Handler handler = new Handler();

        APIGatewayV2HTTPResponse result = handler.handleRequest(createSyntheticAPIGatewayV2HTTPEvent(), createSyntheticContext());

        Assertions.assertEquals(200, result.getStatusCode());
    }

    private Context createSyntheticContext() {
        return (Context)Proxy.newProxyInstance(Context.class.getClassLoader(), new Class[]{Context.class}, (proxy, method, args) -> {
            if (method.getReturnType().equals(String.class)) return method.getName();
            if (method.getReturnType().equals(int.class)) return 0;
            throw new RuntimeException("Unknown method invoked on AWS Lambda Context: " + method.getName());
        });
    }

    // because a publicly accessible builder so people can test locally would be too convinient
    private APIGatewayV2HTTPEvent createSyntheticAPIGatewayV2HTTPEvent() {
        return new APIGatewayV2HTTPEvent();
//        return (APIGatewayV2HTTPEvent) Proxy.newProxyInstance(APIGatewayV2HTTPEvent.class.getClassLoader(), new Class[]{APIGatewayV2HTTPEvent.class}, (proxy, method, args) -> {
//            if (method.getReturnType().equals(String.class) return method.getName());
//            throw new RuntimeException("Unknown method invoked on APIGatewayV2HTTPEvent: " + method.getName());
//        });
    }

}
