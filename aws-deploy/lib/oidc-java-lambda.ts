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

// TODO: Allow a way to deploy into an existing vpc
var useVpc = process.env.USE_VPC || false

export class OidcJavaLambda extends cdk.Stack {
  constructor(scope: Construct, id: string, props: StackProps) {
    super(scope, id, {})
    const functionName = 'simple-oidc'

    // allow logs to be torn down later if required
    const logStack = new cdk.NestedStack(this, 'logs')

    // general resources stack
    const resourcesStack = new cdk.NestedStack(this, 'resources')

    // lambda function isolation stack
    const lambdaFnStack = new cdk.NestedStack(this, 'lambda')



    const lambdaLogGroup = new logs.LogGroup(logStack, `${functionName}-LogGroup`, {
      logGroupName: `/com/kncept/${functionName}`,
      retention: logs.RetentionDays.ONE_MONTH,
      removalPolicy: cdk.RemovalPolicy.DESTROY,
    })

    
    let vpc: ec2.Vpc | undefined = undefined
    if (useVpc) {
      vpc = new ec2.Vpc(resourcesStack, `${functionName}-vpc`, {
          vpcName: `${functionName}-vpc`,
        })
    }

    let zone: route53.IHostedZone | undefined
    if(props.baseHostedZone) {
      zone = route53.HostedZone.fromHostedZoneAttributes(resourcesStack, `${functionName}-LookupHostedZone`, {
        hostedZoneId: props.baseHostedZone.id,
        zoneName: props.baseHostedZone.name,
      })
    } else if (props.lambdaHostname !== '') {
      
      zone = new route53.PublicHostedZone(resourcesStack, `${functionName}-HostedZone`, {
        caaAmazon: true,
        zoneName: extractHostedZoneFromHostname(props.lambdaHostname),
      })
      zone.applyRemovalPolicy(cdk.RemovalPolicy.DESTROY)
    }

    const role = new iam.Role(resourcesStack, `${functionName}-Role`, {
      assumedBy: new iam.ServicePrincipal('lambda.amazonaws.com'),
      roleName: functionName,
      description: 'OIDC Lambda role',
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaVPCAccessExecutionRole"),
        iam.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole"),
      ]
    })
    role.applyRemovalPolicy(cdk.RemovalPolicy.DESTROY)

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

    lambdaLogGroup.grantWrite(role)

    const objKey = `aws-service-${calcVersion()}.zip`
    const deployBucket = new s3.Bucket(resourcesStack, `${functionName}-Service`)

    const rootDir = path.join(__dirname, '..', '..')
    const distDir = path.join(rootDir, 'aws-service', 'build', 'distributions')
    const deployedAsset = new s3Deployment.BucketDeployment(resourcesStack, `${functionName}-Deployment`, {
      destinationBucket: deployBucket,
      sources: [
        s3Deployment.Source.asset(distDir),
      ],
      prune: true,
    })

    const handler = new lambda.Function(lambdaFnStack, `${functionName}-Lambda`, {
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

    const restApi = new apigateway.RestApi(lambdaFnStack, `${functionName}-Api`, {
      restApiName: 'Kncept OIDC',
      description: 'Kncept OIDC and Oauth2 Server',
      endpointTypes: [apigateway.EndpointType.REGIONAL],
      minCompressionSize: cdk.Size.bytes(0),
    })

    const handlerIntegration = new apigateway.LambdaIntegration(handler, {
      allowTestInvoke: false
    })

    restApi.root.addProxy({
      defaultIntegration: handlerIntegration
    })

    if (zone) {
      const apiCertificate = new cdk.aws_certificatemanager.Certificate(lambdaFnStack, `${functionName}-Api-Certificate`, {
        domainName: props.lambdaHostname,
        validation: cdk.aws_certificatemanager.CertificateValidation.fromDns(zone)
      })

      const apiDomainNameMountPoint = restApi.addDomainName(`${functionName}-Api-DomainName`, {
        domainName: props.lambdaHostname,
        certificate: apiCertificate,
      })

      new route53.CnameRecord(lambdaFnStack, `${functionName}-DnsEntry`, {
        zone,
        recordName: props.lambdaHostname.substring(0, props.lambdaHostname.length - (extractHostedZoneFromHostname(props.lambdaHostname).length + 1)),
        domainName: apiDomainNameMountPoint!.domainNameAliasDomainName,
      })
    }
  }

  getLogicalId(element: cdk.CfnElement): string {
    if (element.node.id.includes('NestedStackResource')) {
        return /([a-zA-Z0-9]+)\.NestedStackResource/.exec(element.node.id)![1] // will be the exact id of the stack
    }
    return super.getLogicalId(element)
}
}
