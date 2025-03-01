package com.ksonni.footballdb.queryparser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Fields that have been annotated as NonQueryable will be ignored by the QueryParser.
 * This is used to mainly prevent sensitive information being queried by the user.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NonQueryable {
}
