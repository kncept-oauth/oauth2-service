package com.kncept.oauth2.subhandler;

import com.kncept.oauth2.config.Oauth2Configuration;
import com.kncept.oauth2.config.parameter.ConfigParameters;
import com.kncept.oauth2.operation.response.RenderedContentResponse;
import com.kncept.oauth2.util.MapBuilder;

import java.util.List;
import java.util.Optional;

import static com.kncept.oauth2.util.JsonUtils.toJson;

public class DiscoveryHandler {

    private final Oauth2Configuration config;

    public DiscoveryHandler(Oauth2Configuration config) {
        this.config = config;
    }

    // field names from https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata
    public RenderedContentResponse discovery() {
        // N.B.  "jwks_uri": "certUri" << TODO !!
        return new RenderedContentResponse(
                200,
                toJson(new MapBuilder()
                        .with("issuer", ConfigParameters.issuerName.get(config.parameterRepository())) // MUST be a URL
                        .with("authorization_endpoint", "/authorize")
                        .with("token_endpoint", "/token")
//                        userinfo_endpoint // MUST be https if used
//                        jwks_uri // TODO: REQUIRED
//                        registration_endpoint // Dynamic client Registration?
                        .with("scopes_supported", List.of("openid", "email", "sub")) // MUST include openid
                        .with("response_types_supported", List.of("code", "id_token", "token id_token"))
                        .with("grant_types_supported", List.of("implicit", "authorization_code")) // optional
                        .with("subject_types_supported", List.of("public"))
                        .with("id_token_signing_alg_values_supported", List.of("RS256"))

                        .with("claims_supported", List.of("sub", "iss"))

                        .get()),
                "application/json",
                Optional.empty(),
                false
        );
    }

}
