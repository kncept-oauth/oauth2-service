# [Simple-OIDC](https://github.com/kncept-oauth/simple-oidc)
A Lightweight OIDC over OAuth2 Service

The bulk of this project is an integration-ready oidc processor that can be
embedded into an application of choice.

The configuration can be partially or completely extended and configured.
This project includes fully working and deployable solutions, with the option
to customise via environment properties. At the other end of the spectrum
it's possible to integrated directly against the OIDC processor as a library,
and provide custom code level configurations to deal with any use case imaginable.


## Quickstart (aws)
Requires docker and nodejs.
1. Clone this project `git clone git@github.com:kncept-oauth/simple-oidc.git`
1. Build java components `./batect dist` / `batect dist`
1. Configure AWS keys `export AWS_ACCESS_KEY_ID=...` / `set AWS_ACCESS_KEY_ID=...`
1. Deploy `cd aws-deploy && npm i && npm run cdk deploy OidcDockerLambda`

The URL that is output is the URL_BASE to use for endpoints to integrate against.
- Authorize Endpoint: ${URL_BASE}/authorize
- Token Endpoint: ${URL_BASE}/oauth/token

### Run simple dev server locally
This assumes that you also have Java installed on your system
`./gradlew :java-service:run` / `gradlew :java-service:run` will run the java-service on :8080 configured for development
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

The following `oidc_config` classes are shipped:
- `com.kncept.oauth2.config.SystemProperyConfiguration` - Detailed below
- `com.kncept.oauth2.config.DynoDbOauth2Configuration` - in service-aws, DyndoDB tables, including auto table creation.
- `com.kncept.oauth2.config.InMemoryConfiguration` - In memory volatile store, useful for testing

You probably want to extend something like DynoDbOauth2Configuration and override the UserRepository
with your own implementation.

### SystemProperyConfiguration
Set the `oidc_config` system property to `com.kncept.oauth2.config.SystemProperyConfiguration`.
This will vend a [SystemProperyConfiguration](service-implementation/src/main/java/com/kncept/oauth2/config/SystemProperyConfiguration.java)
which needs the following environment properties set:
    - `oidc_config_pkce`  true to require PKCE, false if it is optional
    - `oidc_config_clients` a [ClientRepository](service-interfaces/src/main/java/com/kncept/oauth2/config/client/ClientRepository.java)
    - `oidc_config_authrequests` an [AuthRequestRepository](service-interfaces/src/main/java/com/kncept/oauth2/config/authrequest/AuthRequestRepository.java)
    - `oidc_config_users` a [UserRepository](service-interfaces/src/main/java/com/kncept/oauth2/config/user/UserRepository.java)
    - `oidc_config_sessions` an [OauthSessionRepository](service-interfaces/src/main/java/com/kncept/oauth2/config/session/OauthSessionRepository.java)
    - `oidc_config_authcodes`an [AuthcodeRepository](service-interfaces/src/main/java/com/kncept/oauth2/config/authcode/AuthcodeRepository.java)
    - `oidc_config_parameter`a [ParameterRepository](service-interfaces/src/main/java/com/kncept/oauth2/config/parameter/ParameterRepository.java)


### ParameterRepository configuration
This points to a table that has simple values in it - see the ConfigParameters enum

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
