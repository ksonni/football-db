package com.ksonni.footballdb.queryparser.components;

import com.ksonni.footballdb.queryparser.QueryParseException;

public class InvalidQueryValueException extends QueryParseException {

    /**
     * Exception thrown when parsing the value of a URL query component fails.
     *
     * @param key   The key the value is associated with
     * @param value The value that couldn't be parsed
     */
    public InvalidQueryValueException(final String key, final String value) {
        super(String.format("Invalid query value '%s' for key '%s'", value, key));
    }

}
