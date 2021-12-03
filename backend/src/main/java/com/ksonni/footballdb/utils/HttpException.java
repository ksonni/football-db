package com.ksonni.footballdb.utils;

import org.springframework.http.HttpStatus;

public class HttpException extends Exception {

    private final HttpStatus status;

    public HttpException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpException(HttpStatus status) {
        super(status.getReasonPhrase());
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return status.value() + " - " + super.getMessage();
    }

}
