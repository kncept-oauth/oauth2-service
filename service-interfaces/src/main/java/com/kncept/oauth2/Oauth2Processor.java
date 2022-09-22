package com.kncept.oauth2;

import com.kncept.oauth2.operation.response.ContentResponse;
import com.kncept.oauth2.operation.response.OperationResponse;
import com.kncept.oauth2.operation.response.RenderedContentResponse;

import java.util.Map;
import java.util.Optional;

public interface Oauth2Processor {
    void init(boolean await);

    RenderedContentResponse discovery();

    OperationResponse authorize(Map<String, String> params, Optional<String> oauthSessionId);
    RenderedContentResponse token(Map<String, String> params);

    OperationResponse login(Map<String, String> params, String oauthSessionId);
    OperationResponse signup(Map<String, String> params, String oauthSessionId);

    RenderedContentResponse renderCss();
    RenderedContentResponse render(ContentResponse response);
}
