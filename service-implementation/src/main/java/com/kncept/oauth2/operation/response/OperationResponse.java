package com.kncept.oauth2.operation.response;

public class OperationResponse {
    public final String contentType;
    public final int responseCode;
    public final String responseDetail;

    public OperationResponse(
            String contentType,
            int responseCode,
            String responseDetail
    ) {
        this.contentType = nonNull(contentType);
        this.responseCode = nonZero(responseCode);
        this.responseDetail = nonNull(responseDetail);
    }

    private String nonNull(String value) {
        if (value == null) throw new IllegalStateException();
        return value;
    }

    private int nonZero(int value) {
        if (value == 0) throw new IllegalStateException();
        return value;
    }
}
