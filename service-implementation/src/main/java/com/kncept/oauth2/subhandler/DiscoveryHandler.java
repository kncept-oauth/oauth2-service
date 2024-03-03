package com.kncept.oauth2.subhandler;

import com.kncept.oauth2.config.Oauth2StorageConfiguration;
import com.kncept.oauth2.config.parameter.ConfigParameters;
import com.kncept.oauth2.operation.response.RenderedContentResponse;
import com.kncept.oauth2.util.MapBuilder;

import java.util.List;
import java.util.Optional;

import static com.kncept.oauth2.util.JsonUtils.toJson;

public class DiscoveryHandler {

    private final Oauth2StorageConfiguration config;
    private final String hostedUrl;

    public DiscoveryHandler(
            Oauth2StorageConfiguration config,
            String hostedUrl
            ) {
        this.config = config;
        this.hostedUrl = hostedUrl;
    }

    // field names from https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata
    // found an example: https://acme.eu.auth0.com/.well-known/openid-configuration
    // see also https://swagger.io/docs/specification/authentication/openid-connect-discovery/
    /**
     * /.well-known/openid-configuration
     * https://openid.net/specs/openid-connect-discovery-1_0.html
     * @return
     */
    public RenderedContentResponse openIdDiscovery() {
        MapBuilder mapBuilder = new MapBuilder()
//                .with("issuer", ConfigParameters.issuerName.get(config.parameterRepository())) // MUST be a URL
                .with("issuer", hostedUrl)
                .with("authorization_endpoint", hostedUrl + "authorize")
                .with("token_endpoint", hostedUrl + "oauth/token")
//                        userinfo_endpoint // MUST be https if used
                .with("jwks_uri", hostedUrl + "jwks")
                .with("pkcs8_uri", hostedUrl + "pkcs8")
//                        registration_endpoint // Dynamic client Registration?
                .with("scopes_supported", List.of("openid", "email", "sub")) // MUST include openid
                .with("response_types_supported", List.of("code", "id_token", "token id_token"))
                .with("grant_types_supported", List.of("implicit", "authorization_code")) // optional //  refresh_token ?
                .with("subject_types_supported", List.of("public"))
                .with("id_token_signing_alg_values_supported", List.of("RS256", "ES256")) // EdDSA
//                .with("id_token_signing_alg_values_supported", List.of("RS256", "RS384"))
                .with("claims_supported", List.of("sub", "iss"));

//        end_session_endpoint oidc/logout  // https://auth0.com/docs/authenticate/login/logout/log-users-out-of-auth0  ?logout_hint=SESSION_ID ?
//        device_authorization_endpoint device/code
//        revocation_endpoint // is this standard?

        // non standard claims:
        mapBuilder.with("login_endpoint", hostedUrl + "login");
        if(acceptingSignup()) mapBuilder.with("signup_endpoint", hostedUrl + "signup");

        return new RenderedContentResponse(
                200,
                toJson(mapBuilder.get()),
                "application/json",
                Optional.empty(),
                false
        );
    }

    /**
     * /.well-known/oauth-authorization-server
     * https://www.rfc-editor.org/rfc/rfc8414.html
     * @return
     */
    public RenderedContentResponse oauthDiscovery() {
        return openIdDiscovery(); // TODO
    }

    boolean acceptingSignup() {
        return Boolean.valueOf(ConfigParameters.signupEnabled.get(config.parameterRepository()));
    }

}
