package com.ksonni.footballdb.queryapi;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class DateFilterQueryComponent<T> implements FilterQueryComponent<T> {

    private final FilterQueryKey key;
    private final ZonedDateTime value;

    DateFilterQueryComponent(FilterQueryKey key, String value) throws InvalidQueryValueException {
        this.key = key;
        try {
            this.value = ZonedDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        } catch(DateTimeParseException e) {
            throw new InvalidQueryValueException(key.getField(), value);
        }
    }
}
