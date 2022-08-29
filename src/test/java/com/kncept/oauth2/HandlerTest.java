package com.kncept.oauth2;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;

public class HandlerTest {


    @Test
    public void canInvokeHandler() {
        Handler handler = new Handler();

        String result = handler.handleRequest(new HashMap<>(), createSyntheticContext());

        Assertions.assertEquals("200 OK", result);
    }

    private Context createSyntheticContext() {
        return (Context)Proxy.newProxyInstance(Context.class.getClassLoader(), new Class[]{Context.class}, (proxy, method, args) -> {
            throw new RuntimeException("Unknown method invoked on AWS Lambda Context: " + method.getName());
        });
    }

}
