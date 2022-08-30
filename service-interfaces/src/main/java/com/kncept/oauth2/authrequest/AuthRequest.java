package com.kncept.oauth2.authrequest;

public interface AuthRequest {

    String getState(); // nullable
    String getRedirectUri();
    String getClientId();
}
