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
1. Configure AWS keys. eg:
  ```
  export AWS_ACCESS_KEY_ID=xxx
  export AWS_SECRET_ACCESS_KEY=xxx
  export AWS_REGION=xxx
  ```
1. Set configuration keys for Lambda control. eg:
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
    Oauth2Configuration config = EnvPropertyConfiguration.loadStorageConfigFromEnvProperty()
    Oauth2Processor oauth2 = new Oauth2(config, hostedUrl)
    Oauth2AutoRouter router = new Oauth2AutoRouter(oauth2)
Then hook up the the Oauth2Processor interface to your web application.
Usage of the router is optional, but recommended as a way to rapidly integrate.
In order to use a different base path, simply append it to the hostedUrl.
N.B. provider specific libraries (eg: AWS Dynamo DB config) will be in provider specific jars.

## Using a prepared solution
The prepared solutions read from the `OIDC_Storage_Config` environment property for storage config, 
and the `OIDC_Hostname` property to know where they are deployed.
All other properties available are defaulted when blank. See the [EnvPropertyConfiguration](service-interfaces/src/main/java/com/kncept/oauth2/config/EnvPropertyConfiguration.java) class for details


Note that if you intend to parially override an existing class, it must be on the
classpath of the running application to be usable.

The following `OIDC_Config` classes are shipped (provider specific class in the provider specific jar:
- `com.kncept.oauth2.config.InMemoryConfiguration` - In memory volatile store, useful for testing
- `com.kncept.oauth2.config.DynoDbOauth2Configuration` - in service-aws, DynamoDB tables

You probably want to extend something like DynoDbOauth2Configuration and override the UserRepository
with your own implementation.


### ParameterRepository configuration
This points to a table that has simple values in it - see the [ConfigParameters](service-interfaces/src/main/java/com/kncept/oauth2/config/parameter/ConfigParameters.java) enum

### Key Manager configuration
Set the `OIDC_Key_Config` system property to a valid implementation.
This is currently WIP


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
If you do any bugfixes, submit a patch :)

Otherwise, the github workflow will need configuration (AWS keys as secrets, control values as variables, as above), and will deploy on commits to master.
