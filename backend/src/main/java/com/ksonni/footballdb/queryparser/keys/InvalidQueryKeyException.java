package com.ksonni.footballdb.queryparser.keys;

import com.ksonni.footballdb.queryparser.QueryParseException;

public class InvalidQueryKeyException extends QueryParseException {

    public InvalidQueryKeyException(String key) {
        super(String.format("Invalid query key \"%s\"", key));
    }

}