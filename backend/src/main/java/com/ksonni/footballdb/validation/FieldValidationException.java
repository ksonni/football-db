package com.ksonni.footballdb.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FieldValidationException extends RuntimeException {

    private final List<FieldError> errors;

}
