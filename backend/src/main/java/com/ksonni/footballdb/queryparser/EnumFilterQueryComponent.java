package com.ksonni.footballdb.queryparser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class EnumFilterQueryComponent<T, U extends Enum<?>> implements FilterQueryComponent<T> {

    private final FilterQueryKey key;
    private final Comparable<?> value;

    public EnumFilterQueryComponent(FilterQueryKey key, String value) throws InvalidQueryValueException {
        this.key = key;
        if (key.getComparison() == Comparison.CONTAINS) {
            this.value = value;
        } else {
            this.value = tryParseEnum(value);
        }
    }

    private U tryParseEnum(String value) throws InvalidQueryValueException {
        try {
            return parseEnum(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidQueryValueException(key.getField(), value);
        }
    }

    public abstract U parseEnum(String value) throws IllegalArgumentException;

}
