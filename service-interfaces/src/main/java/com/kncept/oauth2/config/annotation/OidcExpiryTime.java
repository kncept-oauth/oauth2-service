package com.kncept.oauth2.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * This might be different between implmentations
 * eg: AWS DynamoDB uses epoch SECONDS
 */
public @interface OidcExpiryTime {
}
