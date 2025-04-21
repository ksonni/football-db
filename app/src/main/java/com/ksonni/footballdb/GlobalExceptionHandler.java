package com.ksonni.footballdb;

import com.ksonni.footballdb.query.FilterParseException;
import com.ksonni.footballdb.query.SortParseException;
import com.ksonni.footballdb.validation.ErrorResponse;
import com.ksonni.footballdb.validation.FieldError;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Intercepts request validation exceptions to provide a cleaner response.
     *
     * @param ex Validation exception
     * @return Error response
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(final MethodArgumentNotValidException ex) {
        final List<FieldError> errors = ex.getFieldErrors().stream()
                .map(e -> new FieldError(e.getField(), e.getDefaultMessage()))
                .collect(Collectors.toList());

        final var response = new ErrorResponse(HttpStatus.BAD_REQUEST, null, errors);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    /**
     * Intercepts integrity constraint violation exceptions to provide a cleaner response.
     *
     * @param ex DataIntegrityViolationException exception
     * @return Error response
     */
    @ResponseBody
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrityErrors(final DataIntegrityViolationException ex) {
        final var response = new ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Request violates data constraints",
            new ArrayList<>()
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    /**
     * Intercepts FilterParseException and maps it to a GraphQL error response to present to the client.
     *
     * @param ex FilterParseException
     * @param env DataFetchingEnvironment of the GraphQL query
     * @return corresponding GraphQLError
     */
    @GraphQlExceptionHandler
    public GraphQLError handleFilterParseError(final FilterParseException ex, final DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)
            .message(String.format("Invalid filter: %s", ex.getMessage()))
            .errorType(ErrorType.BAD_REQUEST)
            .build();
    }

    /**
     * Intercepts SortParseException and maps it to a GraphQL error response to present to the client.
     *
     * @param ex SortParseException
     * @param env DataFetchingEnvironment of the GraphQL query
     * @return corresponding GraphQLError
     */
    @GraphQlExceptionHandler
    public GraphQLError handleSortParseError(final SortParseException ex, final DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)
            .message("Invalid sort: " + ex.getMessage())
            .errorType(ErrorType.BAD_REQUEST)
            .build();
    }

}
