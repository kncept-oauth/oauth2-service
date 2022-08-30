package com.kncept.oauth2.operation.response;

public class OperationResponse {

    public enum ResponseType {
        OK_HTML, OK_JSON, ERROR_HTML, REDIRECT
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
