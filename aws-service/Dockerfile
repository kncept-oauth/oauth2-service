# https://sigpwned.com/2022/07/23/aws-lambda-base-images-for-java-17/

FROM public.ecr.aws/aleph0io/lambda/java:17.0.4-al2
COPY build/libs/ "${LAMBDA_TASK_ROOT}/lib/"
CMD [ "com.kncept.oauth2.Handler::handleRequest" ]
