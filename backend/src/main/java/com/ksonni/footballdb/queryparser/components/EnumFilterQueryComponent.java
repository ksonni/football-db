package com.ksonni.footballdb.queryparser.components;

import com.ksonni.footballdb.queryparser.keys.Comparison;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class EnumFilterQueryComponent<T, U extends Enum<?>> implements FilterQueryComponent<T> {

    private final FilterQueryKey key;
    private final Comparable<?> value;

    /**
     * Parses a URL query component used to lookup enums.
     *
     * @param key   Parsed key of a URL query
     * @param value String value of a URL query
     * @throws InvalidQueryValueException if the value could not be parsed as the desired enum
     */
    public EnumFilterQueryComponent(final FilterQueryKey key, final String value) throws InvalidQueryValueException {
        this.key = key;
        if (key.getComparison() == Comparison.CONTAINS) {
            this.value = value;
        } else {
            this.value = tryParseEnum(value);
        }
    }

    private U tryParseEnum(final String val) throws InvalidQueryValueException {
        try {
            return parseEnum(val);
        } catch (IllegalArgumentException e) {
            throw new InvalidQueryValueException(key.getField(), val);
        }
    }

    /**
     * Parses a string value to the desired enum.
     *
     * @param val String value
     * @return The parsed enum
     * @throws IllegalArgumentException if the string could not be parsed to the desired enum
     */
    public abstract U parseEnum(String val) throws IllegalArgumentException;

}
