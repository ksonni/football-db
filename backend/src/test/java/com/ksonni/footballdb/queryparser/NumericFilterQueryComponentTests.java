package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import com.ksonni.footballdb.queryparser.components.NumericFilterQueryComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumericFilterQueryComponentTests {

    @Test
    void parsesDecimalQueries() throws QueryParseException {
        var component = new NumericFilterQueryComponent<Object>(new FilterQueryKey("test"), "12.3");
        assertEquals((Double) component.getValue(), 12.3, 0.001);
    }

    @Test
    void parsesIntegralValues() throws QueryParseException {
        var component = new NumericFilterQueryComponent<Object>(new FilterQueryKey("test"), "12");
        assertEquals((Long) component.getValue(), 12);
    }

}
