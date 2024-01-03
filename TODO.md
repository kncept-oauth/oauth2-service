TODO list:
  
- client secret validation

- refresh tokens

- Auto Key Rotation

- enable proper PKCE

- ability to set or autodetect deployment root
    - this may be specific to implementations (java, aws-lambda, azure-function, etc)

- Clients (as in client config in this app) need to have the ability to validate
    - Allowed Referrers
    - Allowed Redirect URI's
    - eg: 'exact', 'prefix' and (expensive) 'regex' for (type, value)
