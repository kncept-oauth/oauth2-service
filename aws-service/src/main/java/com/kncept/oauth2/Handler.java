package com.kncept.oauth2;

import java.util.Map;
import java.util.TreeMap;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;

public class Handler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    @Override
    // APIGatewayV2ProxyResponseEvent
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        // LambdaLogger logger = context.getLogger();

        StringBuilder sb = new StringBuilder();
        System.getenv().forEach((key, value) -> sb.append("env " + key + outputValue(value)));
        System.getProperties().forEach((key, value) -> sb.append("prop " + key + outputValue(value)));

        sb.append("\n");

        sb.append("logStreamName " + outputValue(context.getLogStreamName()));
        sb.append("awsRequestId " + outputValue(context.getAwsRequestId()));
        sb.append("remainingMillisTime " + outputValue(context.getRemainingTimeInMillis()));

        sb.append("rawPath " + outputValue(input.getRawPath()));
        sb.append("rawQueryString " + outputValue(input.getRawQueryString()));

        sb.append("version " + outputValue(input.getVersion()));
        try { // if (input.getRequestContext().getHttp() != null) { // can't even CHECK for null without the NPE being thrown
            sb.append("httpPath " + outputValue(input.getRequestContext().getHttp().getPath()));
            sb.append("httpMethod " + outputValue(input.getRequestContext().getHttp().getMethod()));
        } catch (NullPointerException e) {
            // because amazon thinks it's clever throwing an NPE and not allowing it to be checked :/
        }

        sb.append("b64Encoded " + outputValue(input.getIsBase64Encoded()));

        sb.append("body " + outputValue(input.getBody()));

        context.getLogger().log(sb.toString());
        System.out.println("sysout:  " + context.toString());

        Map<String, String> headers = new TreeMap<>();
        headers.put("Content-Type", "text/plain");
        APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();
        response.setHeaders(headers);
        response.setIsBase64Encoded(false);
        response.setBody(sb.toString());
        response.setStatusCode(200);
        return response;
    }

    private static String outputValue(Object value) {
        if (value == null) return " is null" + "\n";
        if (value.equals("")) return " is blank" + "\n";
        return " = " + value + "\n";
    }

}
