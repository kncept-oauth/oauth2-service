package com.kncept.oauth2.operation.response;

// this is an oversimplification
public class OperationResponse {

    public enum ResponseType {
        OK_HTML, OK_JSON, ERROR_HTML, CLIENT_ERROR_JSON, REDIRECT
    }

    public final ResponseType type;
    public final String responseDetail;

    public OperationResponse(
            ResponseType type,
            String responseDetail
    ) {
        this.type = type;
        this.responseDetail = responseDetail;
    }

}
