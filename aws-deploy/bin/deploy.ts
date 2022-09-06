#!/usr/bin/env node
import 'source-map-support/register'
import * as cdk from 'aws-cdk-lib'
import { OidcJavaLambda } from '../lib/oidc-java-lambda'
import { OidcDockerLambda } from '../lib/oidc-docker-lambda'

import * as fs from 'fs'
import * as path from 'path'

async function run() {
    const app = new cdk.App();
    // new OidcJavaLambda(app, 'OidcJavaLambda', {})
    new OidcDockerLambda(app, 'OidcDockerLambda', {})
}

run()
