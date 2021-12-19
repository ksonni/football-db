package com.ksonni.footballdb.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FieldError {
    private final String field;
    private final String message;
}
