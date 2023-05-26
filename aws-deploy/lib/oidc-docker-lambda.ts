import * as cdk from 'aws-cdk-lib'
import { Construct } from 'constructs'
import * as apigateway from 'aws-cdk-lib/aws-apigateway'
import * as lambda from 'aws-cdk-lib/aws-lambda'
import * as iam from 'aws-cdk-lib/aws-iam'
import * as logs from 'aws-cdk-lib/aws-logs'


import * as path from 'path'
import { CfnOutput } from 'aws-cdk-lib'

export class OidcDockerLambda extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props)

    const functionName = 'oidc-docker-service'

    const role = new iam.Role(this, 'Role', {
      assumedBy: new iam.ServicePrincipal('lambda.amazonaws.com'),
      roleName: `kncept-${functionName}`,
      description: 'OIDC Lambda role',
    })

    // eg: https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_examples_lambda-access-dynamodb.html
    role.addToPrincipalPolicy(new iam.PolicyStatement({
      sid: 'KnceptOidcDdbTables',
      actions: [
        'dynamodb:*',
      ],
      resources: [
        'arn:aws:dynamodb:*:*:table/KnceptOidc*'
      ],
      effect: iam.Effect.ALLOW,
    }))

    const lambdaLogGroup = new logs.LogGroup(this, 'log-group', {
      logGroupName: `/aws/lambda/${functionName}`,
      retention: logs.RetentionDays.ONE_MONTH,
    })

    lambdaLogGroup.grantWrite(role)

    const rootDir = path.join(__dirname, '..', '..')
    const awsServiceDir = path.join(rootDir, 'aws-service')
    const handler = new lambda.DockerImageFunction(this, 'kncept-oidc-lambda', {
      code: lambda.DockerImageCode.fromImageAsset(path.join(awsServiceDir), {
        file: 'Dockerfile'
      }
      ),
      functionName,
      description: `Kncept OIDC Lambda`,
      timeout: cdk.Duration.seconds(29), // API Gateway 30sec timeout
      environment: {
        // can't have dots . or dashes -
        'OIDC_Config': 'com.kncept.oauth2.config.DynoDbOauth2Configuration',
      },
      role,
      logRetention:  logs.RetentionDays.ONE_MONTH
    })

    const api = new apigateway.RestApi(this, 'kncept-oidc-api', {
      restApiName: 'Kncept OIDC',
      description: 'Oauth2 OIDC Server',
    })
    new CfnOutput(this, 'api-internal-url', {
      value: api.url
    })

    const handlerIntegration = new apigateway.LambdaIntegration(handler, {})

    api.root.addProxy({
      defaultIntegration: handlerIntegration
    })
  }
}
