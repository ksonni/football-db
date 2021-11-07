package com.ksonni.footballdb.queryparser;

public class InvalidQueryKeyException extends QueryParseException {

    public InvalidQueryKeyException(String key) {
        super(String.format("Invalid query key \"%s\"", key));
    }

}