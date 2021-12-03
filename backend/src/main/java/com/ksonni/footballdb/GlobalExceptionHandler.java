package com.ksonni.footballdb;

import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.utils.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(QueryParseException.class)
    public ResponseEntity<String> handleInvalidQueries(HttpServletRequest request, Exception ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpException.class})
    public ResponseEntity<String> handleHttpException(HttpException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.getStatus());
    }

}
