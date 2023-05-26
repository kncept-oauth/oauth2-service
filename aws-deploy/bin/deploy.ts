#!/usr/bin/env node
import 'source-map-support/register'
import * as cdk from 'aws-cdk-lib'
import { OidcJavaLambda } from '../lib/oidc-java-lambda'
import { matchHostedZoneToFQDN } from './domain-tools'

function envVarBooleanValue(varName: string): boolean {
    const val = process.env[varName] || ''
    if (val.toLowerCase().trim() === 'true') return true
    return false
}

const lambdaHostname = process.env.LAMBDA_HOSTNAME || ''
const lookupBasename = envVarBooleanValue('LOOKUP_BASENAME')

async function run() {
    const app = new cdk.App()

    if (lookupBasename) {
        const baseHostedZone = await matchHostedZoneToFQDN(lambdaHostname)
        if (baseHostedZone === undefined) {
            throw new Error(`Unable to find existing basename of ${lambdaHostname}`)
        }
        new OidcJavaLambda(app, 'OidcJavaLambda', {
            baseHostedZone,
            lambdaHostname,
        })
    } else {
        new OidcJavaLambda(app, 'OidcJavaLambda', {
            lambdaHostname,
        })
    }

    

}

run()
