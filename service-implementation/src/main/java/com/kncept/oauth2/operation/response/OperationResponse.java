package com.kncept.oauth2.operation.response;

public interface OperationResponse {

    int responseCode();

    default boolean isRenderedContentResponse() {
        return RenderedContentResponse.class.isAssignableFrom(getClass());
    }
    default RenderedContentResponse asRenderedContentResponse() {
        return (RenderedContentResponse) this;
    }

    default boolean isRedirect() {
        return RedirectResponse.class.isAssignableFrom(getClass());
    }
    default RedirectResponse asRedirect() {
        return (RedirectResponse) this;
    }

    default boolean isContent() {
        return ContentResponse.class.isAssignableFrom(getClass());
    }
    default ContentResponse asContent() {
        return (ContentResponse) this;
    }
}
