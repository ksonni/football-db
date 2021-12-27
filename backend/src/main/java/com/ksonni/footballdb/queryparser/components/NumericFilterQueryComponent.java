package com.ksonni.footballdb.queryparser.components;

import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import com.ksonni.footballdb.utils.MathUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class NumericFilterQueryComponent<T> implements FilterQueryComponent<T> {

    private final FilterQueryKey key;
    private final Comparable value;

    /**
     * Parses URL query components used to lookup numbers.
     *
     * @param key      Parsed key of a URL filter query
     * @param strValue String value of a URL query
     * @throws InvalidQueryValueException if the value could not be parsed as a datetime
     */
    public NumericFilterQueryComponent(final FilterQueryKey key, final String strValue)
            throws InvalidQueryValueException {
        final Comparable val;
        if (strValue.contains(".")) {
            val = MathUtils.tryParse(Double::parseDouble, strValue);
        } else {
            val = MathUtils.tryParse(Long::parseLong, strValue);
        }
        if (val == null) {
            throw new InvalidQueryValueException(key.getField(), strValue);
        }
        this.key = key;
        this.value = val;
    }

}
