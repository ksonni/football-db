package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.queryparser.components.NumericFilterQueryComponent;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NumericFilterFilterComponentTests {

    @Test
    void parsesDecimalQueries() throws QueryParseException {
        final var component = new NumericFilterQueryComponent<Object>(new FilterQueryKey("test"), "12.3");
        final var expected = 12.3;
        final var delta = 0.001;
        Assertions.assertEquals(expected, (Double) component.getValue(), delta);
    }

    @Test
    void parsesIntegralValues() throws QueryParseException {
        final var component = new NumericFilterQueryComponent<Object>(new FilterQueryKey("test"), "12");
        final var expected = 12;
        Assertions.assertEquals(expected, (Long) component.getValue());
    }

}
