import * as cdk from 'aws-cdk-lib'
import { Construct } from 'constructs'
import * as apigateway from 'aws-cdk-lib/aws-apigateway'
import * as lambda from 'aws-cdk-lib/aws-lambda'
import * as iam from 'aws-cdk-lib/aws-iam'
import * as s3 from 'aws-cdk-lib/aws-s3'
import * as logs from 'aws-cdk-lib/aws-logs'
import * as ec2 from 'aws-cdk-lib/aws-ec2'
import * as route53 from 'aws-cdk-lib/aws-route53'
import * as s3Deployment from 'aws-cdk-lib/aws-s3-deployment'


import * as path from 'path'
import { CfnOutput } from 'aws-cdk-lib'
import { HostedZoneInfo, extractHostedZoneFromHostname } from '../bin/domain-tools'
import { calcVersion } from '../bin/deploy'

export interface StackProps {
  baseHostedZone?: HostedZoneInfo | undefined
  lambdaHostname: string
}

export class OidcJavaLambda extends cdk.Stack {
  constructor(scope: Construct, id: string, props: StackProps) {
    super(scope, id, {})

    const functionName = 'kncept-oidc'

    const vpc = new ec2.Vpc(this, `${functionName}-vpc`, {
      vpcName: `${functionName}-vpc`,
    })

    let zone: route53.IHostedZone | undefined
    if(props.baseHostedZone) {
      zone = route53.HostedZone.fromHostedZoneAttributes(this, `${functionName}-LookupHostedZone`, {
        hostedZoneId: props.baseHostedZone.id,
        zoneName: props.baseHostedZone.name,
      })
    } else if (props.lambdaHostname !== '') {
      zone = new route53.PublicHostedZone(this, `${functionName}-HostedZone`, {
        caaAmazon: true,
        zoneName: extractHostedZoneFromHostname(props.lambdaHostname),
      })
    }

    const role = new iam.Role(this, `${functionName}-Role`, {
      assumedBy: new iam.ServicePrincipal('lambda.amazonaws.com'),
      roleName: functionName,
      description: 'OIDC Lambda role',
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaVPCAccessExecutionRole"),
        iam.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole"),
      ]
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

    const lambdaLogGroup = new logs.LogGroup(this, `${functionName}-LogGroup`, {
      logGroupName: `/aws/lambda/${functionName}`,
      retention: logs.RetentionDays.ONE_MONTH,
    })

    lambdaLogGroup.grantWrite(role)

    const objKey = `aws-service-${calcVersion()}.zip`
    const deployBucket = new s3.Bucket(this, `${functionName}-Service`)

    const rootDir = path.join(__dirname, '..', '..')
    const distDir = path.join(rootDir, 'aws-service', 'build', 'distributions')
    const deployedAsset = new s3Deployment.BucketDeployment(this, `${functionName}-Deployment`, {
      destinationBucket: deployBucket,
      sources: [
        s3Deployment.Source.asset(distDir),
      ],
      prune: true,
    })

    const handler = new lambda.Function(this, `${functionName}-Lambda`, {
      runtime: lambda.Runtime.JAVA_21,
      functionName: 'oidc-service',
      code: lambda.Code.fromBucket(deployedAsset.deployedBucket, objKey),
      handler: 'com.kncept.oauth2.Handler',
      timeout: cdk.Duration.seconds(29), // behind api gateway = 29s timeout
      environment: {
        // can't have dots . or dashes -
        
        // use the (default) DDB table for config and control
          'OIDC_Storage_Config': 'com.kncept.oauth2.config.DynoDbOauth2Configuration',
      },
      role,
      logRetention:  logs.RetentionDays.ONE_MONTH,
      vpc
    })

    const restApi = new apigateway.RestApi(this, `${functionName}-Api`, {
      restApiName: 'Kncept OIDC',
      description: 'Kncept OIDC and Oauth2 Server',
      endpointTypes: [apigateway.EndpointType.REGIONAL],
      minCompressionSize: cdk.Size.bytes(0),
    })
    new CfnOutput(this, `${functionName}-Api-Url-Backend`, {
      value: restApi.url
    })

    const handlerIntegration = new apigateway.LambdaIntegration(handler, {
      allowTestInvoke: false
    })

    restApi.root.addProxy({
      defaultIntegration: handlerIntegration
    })

    if (zone) {
      const apiCertificate = new cdk.aws_certificatemanager.Certificate(this, `${functionName}-Api-Certificate`, {
        domainName: props.lambdaHostname,
        validation: cdk.aws_certificatemanager.CertificateValidation.fromDns(zone)
      })

      const apiDomainNameMountPoint = restApi.addDomainName(`${functionName}-Api-DomainName`, {
        domainName: props.lambdaHostname,
        certificate: apiCertificate,
      })

      new route53.CnameRecord(this, `${functionName}-DnsEntry`, {
        zone,
        recordName: props.lambdaHostname.substring(0, props.lambdaHostname.length - (extractHostedZoneFromHostname(props.lambdaHostname).length + 1)),
        domainName: apiDomainNameMountPoint!.domainNameAliasDomainName,
      })

      new CfnOutput(this, `${functionName}-Api-Url-Output`, {
        value: `https://${props.lambdaHostname}/`
      })
    }
  }
}
