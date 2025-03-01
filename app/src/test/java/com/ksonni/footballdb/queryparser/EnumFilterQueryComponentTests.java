package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.queryparser.components.EnumFilterQueryComponent;
import com.ksonni.footballdb.queryparser.components.InvalidQueryValueException;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumFilterQueryComponentTests {

    @Test
    void storesContainsQueriesAsStrings() throws QueryParseException {
        final var component = new TestEnumComponent(new FilterQueryKey("in:test"), "TE");
        Assertions.assertEquals("TE", component.getValue());
    }

    @Test
    void storesNormalQueriesAsEnums() throws QueryParseException {
        final var component = new TestEnumComponent(new FilterQueryKey("test"), "TEST1");
        Assertions.assertEquals(TestEnum.TEST1, component.getValue());
    }

    enum TestEnum {
        TEST1,
        TEST2
    }

    class TestEnumComponent extends EnumFilterQueryComponent<Object, TestEnum> {
        TestEnumComponent(final FilterQueryKey key, final String value) throws InvalidQueryValueException {
            super(key, value);
        }

        @Override
        public TestEnum parseEnum(final String value) throws IllegalArgumentException {
            return TestEnum.valueOf(value);
        }
    }

}
