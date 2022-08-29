package com.kncept.oauth2;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Map<String,String>, String> {

    @Override
    // APIGatewayV2ProxyResponseEvent
    public String handleRequest(Map<String, String> input, Context context) {
        // LambdaLogger logger = context.getLogger();


        System.getenv().forEach((key, value) -> System.out.println("env " + key + outputValue(value)));
        System.getProperties().forEach((key, value) -> System.out.println("prop " + key + outputValue(value)));

        return "200 OK";
    }

    private static String outputValue(Object value) {
        if (value == null) return " is null";
        if (value.equals("")) return " is blank";
        return " = " + value;
    }

}
