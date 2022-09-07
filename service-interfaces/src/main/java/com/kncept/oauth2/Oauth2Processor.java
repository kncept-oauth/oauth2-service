package com.kncept.oauth2;

import com.kncept.oauth2.operation.response.ContentResponse;
import com.kncept.oauth2.operation.response.OperationResponse;
import com.kncept.oauth2.operation.response.RenderedContentResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public interface Oauth2Processor {
    void init(boolean await);

    OperationResponse authorize(Map<String, String> params, Optional<String> oauthSessionId) throws IOException;
    RenderedContentResponse token(Map<String, String> params);

    OperationResponse login(Map<String, String> params, String oauthSessionId) throws IOException;
    OperationResponse signup(Map<String, String> params, String oauthSessionId) throws IOException;

    RenderedContentResponse renderCss();
    RenderedContentResponse render(ContentResponse response);
}
