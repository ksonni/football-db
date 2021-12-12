package com.ksonni.footballdb;

import com.ksonni.footballdb.queryparser.QueryParseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public void handleMyException(QueryParseException e, HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.BAD_REQUEST.value());
    }

}
