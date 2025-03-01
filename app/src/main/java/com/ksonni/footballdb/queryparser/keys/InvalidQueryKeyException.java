package com.ksonni.footballdb.queryparser.keys;

import com.ksonni.footballdb.queryparser.QueryParseException;

public class InvalidQueryKeyException extends QueryParseException {

    /**
     * Thrown if parsing the key of a URL query component fails.
     *
     * @param key The key that could not be parsed
     */
    public InvalidQueryKeyException(final String key) {
        super(String.format("Invalid query key \"%s\"", key));
    }

}
