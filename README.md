# [Simple-OIDC](https://github.com/kncept-oauth/simple-oidc)
An OIDC implementation light enough to deploy in a lambda.

The bulk of this project is an integration-ready oidc processor that can be
embedded into an application of choice.

The configuration can be partially or completely extended and configured.
This project includes fully working and deployable solutions, with the option
to customise via environment properties. At the other end of the spectrum
it's possible to integrated directly against the OIDC processor as a library,
and provide custom code level configurations to deal with any use case imaginable.

## Quickstart (aws)
Assuming you are runing in the devcontainer:
1. Clone this project `git clone git@github.com:kncept-oauth/simple-oidc.git`
1. Build java components `./gradlew dist`
1. Configure AWS keys 
  ```
  export AWS_ACCESS_KEY_ID=xxx
  export AWS_SECRET_ACCESS_KEY=xxx
  export AWS_REGION=ap-southeast-2
  ```
1. Set configuration keys for Lambda control
  ```
  export LAMBDA_HOSTNAME=oidc.kncept.com
  export LOOKUP_BASENAME=true
  ```
1. Deploy `cd aws-deploy && npm i && npm run cdk deploy OidcJavaLambda`

This will result in a URL_BASE of https://${LAMBDA_HOSTNAME}
if LAMBDA_HOSTNAME is not defined, then the default api gateway url will be used.
This goes into the 'URL_BASE' Cloudformation stack output, and can be viewed via the AWS Console.
- Authorize Endpoint: ${URL_BASE}/authorize
- Token Endpoint: ${URL_BASE}/oauth/token

### Run simple dev server locally
This assumes that you also have Java installed on your system
`./gradlew :java-service:run` will run the java-service on :8080 configured for development
- Authorize Endpoint: http://localhost:8080/authorize
- Token Endpoint: http://localhost:8080/oauth/token


# Integration

## As a Library
    Oauth2Configuration config = ...
    Oauth2Processor oauth2 = new Oauth2(config)
Then hook up the the Oauth2Processor interface to your web application.
This makes overriding and extending config trivial, and gives the most flexibility, but requires the most effort.
N.B. provider specific libraries (eg: AWS Dynamo DB config) will be in provider specific jars.

## Using a prepared solution
The prepared solutions read from the `oidc_config` environment property.
This can be configured to point to a full implementation of the Oauth2Configuration
interface, or can be left blank and individual points can be configured.

Note that if you intend to parially override an existing class, it must be on the
classpath of the running application to be usable.

The following `OIDC_Config` classes are shipped (provider specific class in the provider specific jar:
- `com.kncept.oauth2.config.EnvPropertyConfiguration` - Detailed below.
- `com.kncept.oauth2.config.InMemoryConfiguration` - In memory volatile store, useful for testing
- `com.kncept.oauth2.config.DynoDbOauth2Configuration` - in service-aws, DynamoDB tables
  - uses the default SystemProperyConfiguration override points before providing DynamoDB implementations
  - the additional property `OIDC_ConfigAutocreate` can be configured (defaults to true) to control table autocreation. 

You probably want to extend something like DynoDbOauth2Configuration and override the UserRepository
with your own implementation.

### EnvPropertyConfiguration of Data Storage
Set the `OIDC_Config` system property to `com.kncept.oauth2.config.EnvPropertyConfiguration`
This will vend an [EnvPropertyConfiguration](service-implementation/src/main/java/com/kncept/oauth2/config/SystemProperyConfiguration.java)
which needs the following environment properties set:
    - `OIDC_Config_Client` a [ClientRepository](service-interfaces/src/main/java/com/kncept/oauth2/config/client/ClientRepository.java)
    - `OIDC_Config_AuthRequest` an [AuthRequestRepository](service-interfaces/src/main/java/com/kncept/oauth2/config/authrequest/AuthRequestRepository.java)
    - `OIDC_Config_User` a [UserRepository](service-interfaces/src/main/java/com/kncept/oauth2/config/user/UserRepository.java)
    - `OIDC_Config_OauthSession` an [OauthSessionRepository](service-interfaces/src/main/java/com/kncept/oauth2/config/session/OauthSessionRepository.java)
    - `OIDC_Config_Authcode` an [AuthcodeRepository](service-interfaces/src/main/java/com/kncept/oauth2/config/authcode/AuthcodeRepository.java)
    - `OIDC_Config_Parameter` a [ParameterRepository](service-interfaces/src/main/java/com/kncept/oauth2/config/parameter/ParameterRepository.java)
    - `OIDC_Config_ExpiringKeypair` an [ExpiringKeypairRepository](service-interfaces/src/main/java/com/kncept/oauth2/config/crypto/ExpiringKeypairRepository.java)

### ParameterRepository configuration
This points to a table that has simple values in it - see the [ConfigParameters](service-interfaces/src/main/java/com/kncept/oauth2/config/parameter/ConfigParameters.java) enum

### Key Manager configuration
Set the `OIDC_Keys` system property to one of the following:
  - `Preshared`
    - Uses `OIDC_Keys_Public` and `OIDC_Keys_Private` for the PKCS8 encoded values of the keys to use
  - `Static`
    - Will generate a fixed infinite key, and store it in the ExpiringKeypairRepository
  - `Rotating`
    - WIP - Currently unsupported... will rotate the key according to configured Parameters


## AWS Deployment
A default aws deployment is provided in the `aws-deploy` folder

You to use as-is, you will need to `export` or `set` your own deployment keys

    AWS_ACCESS_KEY_ID
    AWS_SECRET_ACCESS_KEY
    AWS_DEFAULT_REGION

You can add your own classes into the Dockerfile, but as long as project-specific code
isn't pushed upstream, you can always modify a repository fork.

## Forked Repo

You can of course fork the repo for yourself.
if you do any bugfixes, submit a patch :)

Otherwise, the github workflow will need configuration (AWS keys, as above), and will deploy to a default (random) URL.
You can see the URL in the outputs of the Cloudformation stack in the AWS console.
