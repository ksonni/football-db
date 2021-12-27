package com.ksonni.footballdb.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ksonni.footballdb.utils.HttpUtils;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private List<FieldError> errors;
    private String path;

    @JsonIgnore
    private HttpStatus httpStatus;

    /**
     * Structure to report field validation errors.
     *
     * @param httpStatus Http status to respond with
     * @param message    Summary of the error
     * @param errors     Field level errors
     */
    public ErrorResponse(final HttpStatus httpStatus, final String message, final List<FieldError> errors) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.errors = errors;

        status = httpStatus.value();
        path = getRequestPath();
        error = httpStatus.getReasonPhrase();
        timestamp = ZonedDateTime.now(ZoneOffset.UTC);
    }

    private String getRequestPath() {
        final HttpServletRequest request = HttpUtils.getCurrentRequest();
        return request.getRequestURI().substring(request.getContextPath().length());
    }

}
