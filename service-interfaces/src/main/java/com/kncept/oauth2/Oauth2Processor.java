package com.kncept.oauth2;

import com.kncept.oauth2.operation.response.ContentResponse;
import com.kncept.oauth2.operation.response.OperationResponse;
import com.kncept.oauth2.operation.response.RenderedContentResponse;

import java.util.Map;
import java.util.Optional;

public interface Oauth2Processor {
    void init(boolean await);

    /**
     * OIDC Discovery endpoint.
     * @return JSON rendering of OIDC doc
     */
    RenderedContentResponse openIdDiscovery();

    /**
     * OAuth Discovery endpoint
     * @return JSON rendering of oauth doc
     */
    RenderedContentResponse oauthDiscovery();

    RenderedContentResponse jwks();
    RenderedContentResponse pkcs8();


    /**
     * Start the signup flow (when enabled)
     * @param params
     * @param oauthSessionId
     * @return
     */
    OperationResponse signup(Map<String, String> params, String oauthSessionId);

    /**
     * Complete the signup flow (when enabled) by verifying
     * @param params
     * @param oauthSessionId
     * @return
     */
    OperationResponse verify(Map<String, String> params, String oauthSessionId);


    OperationResponse authorize(Map<String, String> params, Optional<String> oauthSessionId);
    RenderedContentResponse token(Map<String, String> params);

    OperationResponse login(Map<String, String> params, String oauthSessionId);

    RenderedContentResponse renderCss();
    RenderedContentResponse render(ContentResponse response);
}
