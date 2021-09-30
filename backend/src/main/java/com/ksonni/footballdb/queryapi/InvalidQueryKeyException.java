package com.ksonni.footballdb.queryapi;

public class InvalidQueryKeyException extends Exception {

    public InvalidQueryKeyException(String key) {
        super(String.format("Invalid query key \"%s\"", key));
    }

}