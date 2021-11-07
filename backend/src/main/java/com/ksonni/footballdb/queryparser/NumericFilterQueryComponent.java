package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.utils.MathUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class NumericFilterQueryComponent<T> implements FilterQueryComponent<T> {

    private final FilterQueryKey key;
    private final Double value;

    NumericFilterQueryComponent(FilterQueryKey key, String strValue) throws InvalidQueryValueException {
        Double value = MathUtils.tryParseDouble(strValue);
        if (value == null) {
            throw new InvalidQueryValueException(key.getField(), strValue);
        }
        this.key = key;
        this.value = value;
    }

}
