package com.ksonni.footballdb.queryparser.components;

import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
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

    /**
     * Parses a URL query component used to lookup datetimes.
     *
     * @param key   Parsed key of a URL query
     * @param value String value of a URL query
     * @throws InvalidQueryValueException if the value could not be parsed as a datetime
     */
    public DateFilterQueryComponent(final FilterQueryKey key, final String value) throws InvalidQueryValueException {
        this.key = key;
        try {
            this.value = ZonedDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new InvalidQueryValueException(key.getField(), value);
        }
    }
}
