#!/usr/bin/env node
import 'source-map-support/register'
import * as cdk from 'aws-cdk-lib'
import { OidcJavaLambda } from '../lib/oidc-java-lambda'
import { HostedZoneInfo, matchHostedZoneToFQDN } from './domain-tools'

function envVarBooleanValue(varName: string): boolean {
    const val = process.env[varName] || ''
    if (val.toLowerCase().trim() === 'true') return true
    return false
}

const lambdaHostname = process.env.LAMBDA_HOSTNAME || ''
const lookupBasename = envVarBooleanValue('LOOKUP_BASENAME')

// must match calcVersion() in 'build.gradle'
export function calcVersion() {
    const githubRefName = process.env.GITHUB_REF_NAME || '' 
    if (githubRefName.startsWith("v")) return githubRefName.substring(1)
    return "0.0.0-SNAPSHOT"
}

async function run() {
    const app = new cdk.App()

    let baseHostedZone: HostedZoneInfo | undefined = undefined
    if (lookupBasename) {
        baseHostedZone = await matchHostedZoneToFQDN(lambdaHostname)
        if (baseHostedZone === undefined) {
            throw new Error(`Unable to find existing basename of ${lambdaHostname}`)
        }
        
    }
    new OidcJavaLambda(app, 'simple-oidc', {
        baseHostedZone,
        lambdaHostname,
    })
}

run()
