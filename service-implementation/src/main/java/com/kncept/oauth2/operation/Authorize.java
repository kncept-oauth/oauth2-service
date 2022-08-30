package com.kncept.oauth2.operation;


public class Authorize {

    public static class Request {

        // required
        String client_id;
        String response_type; // ie: code

        // PKCE required
        String code_challenge;
        String code_challenge_method;

        // optional
        String redirect_uri;
        String scope; // any additional (non default) scopes
        String state; // will be presented back to the app



        // optional
//    String nonce;
    }

    public static class Response {
        String htmlPage;
    }



}