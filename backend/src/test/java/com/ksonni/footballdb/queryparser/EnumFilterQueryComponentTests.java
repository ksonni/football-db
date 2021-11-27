package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.queryparser.components.EnumFilterQueryComponent;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import com.ksonni.footballdb.queryparser.components.InvalidQueryValueException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnumFilterQueryComponentTests {

    enum TestEnum { TEST1, TEST2 }

    class TestEnumComponent extends EnumFilterQueryComponent<Object, TestEnum> {
        public TestEnumComponent(FilterQueryKey key, String value) throws InvalidQueryValueException {
            super(key, value);
        }

        @Override
        public TestEnum parseEnum(String value) throws IllegalArgumentException {
            return TestEnum.valueOf(value);
        }
    };

    @Test
    void storesContainsQueriesAsStrings() throws QueryParseException {
        var component = new TestEnumComponent(new FilterQueryKey("in:test"), "TE");
        assertEquals("TE", component.getValue());
    }

    @Test
    void storesNormalQueriesAsEnums() throws QueryParseException {
        var component = new TestEnumComponent(new FilterQueryKey("test"), "TEST1");
        assertEquals(TestEnum.TEST1, component.getValue());
    }

}
