# oauth2-service
OAuth2 Service

A lightweight extensible Oauth2 solution, designed to be able to tie into existing auth systems with minimal configuration. 

Retrofitted for Java11 because AWS doesn't support modern things

## How to use

There are a few different ways to use this project.
The recommended way is to deploy and configure

### Run simple java server locally
    `gradlew :java-service:run` 

### Deploy and Configure
Deploy the .zip file as a lambda, and configure the appropriate configuration options.

### Configuration points:
See [Oauth2Configuration](service-implementation/src/main/java/com/kncept/oauth2/configuration/Oauth2Configuration.java) for details
  - oauth2.require-pkce
    - boolean
    - `true` to force PKCE , otherwise empty or `false`
  - oauth2.client-repository
    - java classname
    - Client Repository Details, defaults to allowing any client id
  - oauth2.authrequest-repository
    - java classname
    - Auth Request Persistence, defaults to in memory.
  - oauth2.user-repository
    - java classname
    - End User Authentication

## Deployment
A default aws deployment is provided in the `aws-deploy` folder

You to use as-is, you will need to `set` or `export` your own deployment keys

    - AWS_ACCESS_KEY_ID
    - AWS_SECRET_ACCESS_KEY
    - AWS_DEFAULT_REGION

If someone wants to build me an equivalent azure one, please do :)


