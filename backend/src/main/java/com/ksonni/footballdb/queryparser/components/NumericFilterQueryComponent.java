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

    public NumericFilterQueryComponent(FilterQueryKey key, String strValue) throws InvalidQueryValueException {
        Comparable value;
        if (strValue.contains(".")) {
            value = MathUtils.tryParse(Double::parseDouble, strValue);
        } else {
            value = MathUtils.tryParse(Long::parseLong, strValue);
        }
        if (value == null) {
            throw new InvalidQueryValueException(key.getField(), strValue);
        }
        this.key = key;
        this.value = value;
    }

}
