package com.ksonni.footballdb.queryparser.keys;

/**
 * Comparisons to use while filtering results.
 */
public enum Comparison {
    /**
     * Looks for values with exact match.
     */
    EQUALS,

    /**
     * Looks for values less than the specified value.
     */
    LESS_THAN,

    /**
     * Looks for values greater than the specified value.
     */
    GREATER_THAN,

    /**
     * Looks for values less than or equal to the specified value.
     */
    LESS_THAN_EQUALS,

    /**
     * Looks for values greater than or equal to the specified value.
     */
    GREATER_THAN_EQUALS,

    /**
     * Looks for values that contain the specified value.
     */
    CONTAINS
}
