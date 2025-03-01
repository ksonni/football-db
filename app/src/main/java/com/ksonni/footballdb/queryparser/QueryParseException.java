package com.ksonni.footballdb.queryparser;

public class QueryParseException extends Exception {
    /**
     * Exception thrown when parsing a URL query fails.
     *
     * @param message reason for the failure
     */
    public QueryParseException(final String message) {
        super(message);
    }
}
