package com.ksonni.footballdb;

import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.validation.ErrorResponse;
import com.ksonni.footballdb.validation.FieldError;
import com.ksonni.footballdb.validation.FieldValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(QueryParseException.class)
    public ResponseEntity<ErrorResponse> handleQueryParseErrors(QueryParseException e) {
        var response = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getFieldErrors().stream()
                .map(e -> new FieldError(e.getField(), e.getDefaultMessage()))
                .collect(Collectors.toList());

        var response = new ErrorResponse(HttpStatus.BAD_REQUEST, null, errors);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ResponseBody
    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(FieldValidationException ex) {
        var response = new ErrorResponse(HttpStatus.BAD_REQUEST, null, ex.getErrors());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
