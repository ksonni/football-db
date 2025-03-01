package com.ksonni.footballdb.queryparser.keys;

/**
 * Logical operation used to aggregate multiple Spring data Specifications.
 */
public enum Aggregator {
    /**
     * Aggregates specs using logical AND.
     */
    AND,

    /**
     * Aggregates specs using logical OR.
     */
    OR
}
