package com.ksonni.footballdb.queryparser.components;

import com.ksonni.footballdb.queryparser.QueryParseException;

public class InvalidQueryValueException extends QueryParseException {

    public InvalidQueryValueException(String key, String value) {
        super(String.format("Invalid query value \"%s\" for key \"%s\"", value, key));
    }

}
