TODO list:
  
- enable proper PKCE

- ability to set or autodetect deployment root
  - this may be specific to implementations (java, aws-lambda, azure-function, etc)

- Clients (as in client config in this app) need to have the ability to validate
  - Allowed Referrers
  - Allowed Redirect URI's
  - eg: 'exact', 'prefix' and (expensive) 'regex' for (type, value)
  
- split out a password handler, to enable customisable pre-hashing of passwords
