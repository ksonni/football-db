package com.ksonni.footballdb.queryapi;

public class InvalidQueryValueException extends QueryParseException {

    public InvalidQueryValueException(String key, String value) {
        super(String.format("Invalid query value \"%s\" for key \"%s\"", value, key));
    }

}
