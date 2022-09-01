import * as cdk from 'aws-cdk-lib'
import { Construct } from 'constructs'
import * as apigateway from 'aws-cdk-lib/aws-apigateway'
import * as lambda from 'aws-cdk-lib/aws-lambda'
import * as iam from 'aws-cdk-lib/aws-iam'
import * as s3 from 'aws-cdk-lib/aws-s3'
import * as logs from 'aws-cdk-lib/aws-logs'
import * as s3Deployment from 'aws-cdk-lib/aws-s3-deployment'


import * as path from 'path'
import { CfnOutput } from 'aws-cdk-lib'

export class OidcJavaLambda extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const role = new iam.Role(this, 'Role', {
      assumedBy: new iam.ServicePrincipal('lambda.amazonaws.com'),
      roleName: 'kncept-oauth',
      description: 'OIDC Lambda role',
    })

    const objKey = 'aws-service-0.0.1.zip'
    const deployBucket = new s3.Bucket(this, 'kncept-oidc-service')

    const rootDir = path.join(__dirname, '..', '..')
    const distDir = path.join(rootDir, 'aws-service', 'build', 'distributions')
    const deployedAsset = new s3Deployment.BucketDeployment(this, 'deployment', {
      destinationBucket: deployBucket,
      sources: [
        // s3Deployment.Source.asset(path.join(distDir, 'aws-service-0.0.1.zip'))
        s3Deployment.Source.asset(distDir),
      ],
      prune: true,
    })

    const handler = new lambda.Function(this, 'kncept-oidc-lambda', {
      runtime: lambda.Runtime.JAVA_11,
      functionName: 'oidc-java-service',
      code: lambda.Code.fromBucket(deployedAsset.deployedBucket, objKey),
      handler: 'com.kncept.oauth2.Handler',
      timeout: cdk.Duration.seconds(30),
      environment: {
        // can't have dots . or dashes -
        // :(
        'oidc_test_property': 'com.kncept.xxx.Xyz'
      },
      role,
      logRetention:  logs.RetentionDays.ONE_MONTH
    })

    const api = new apigateway.RestApi(this, "kncept-oidc-api", {
      restApiName: "Kncept OIDC",
      description: "Oauth2 OIDC Server"
    })
    new CfnOutput(this, 'api-internal-url', {
      value: api.url
    })

    const handlerIntegration = new apigateway.LambdaIntegration(handler, {
      //requestTemplates: { "application/json": '{ "statusCode": "200" }' }
    })

    api.root.addMethod("GET", handlerIntegration)
    api.root.addMethod("POST", handlerIntegration)

  }
}
