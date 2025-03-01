package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.queryparser.keys.Aggregator;
import com.ksonni.footballdb.queryparser.keys.Comparison;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import com.ksonni.footballdb.queryparser.keys.InvalidQueryKeyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FilterQueryKeyTests {

    @Test
    void parsesQueryKey() throws InvalidQueryKeyException {
        final var key = new FilterQueryKey("or:eq:someField");
        Assertions.assertEquals(Aggregator.OR, key.getAggregator());
        Assertions.assertEquals(Comparison.EQUALS, key.getComparison());
        Assertions.assertEquals("someField", key.getField());
    }

    @Test
    void parsesWithNoModifiers() throws InvalidQueryKeyException {
        final var key = new FilterQueryKey("someField");
        Assertions.assertEquals(Aggregator.AND, key.getAggregator());
        Assertions.assertEquals(Comparison.EQUALS, key.getComparison());
        Assertions.assertEquals("someField", key.getField());
    }

    @Test
    void parsesOnlyComparisonModifier() throws InvalidQueryKeyException {
        final var key = new FilterQueryKey("lt:someField");
        Assertions.assertEquals(Aggregator.AND, key.getAggregator());
        Assertions.assertEquals(Comparison.LESS_THAN, key.getComparison());
        Assertions.assertEquals("someField", key.getField());
    }

    @Test
    void parsesOnlyAggregatorModifier() throws InvalidQueryKeyException {
        final var key = new FilterQueryKey("or:someField");
        Assertions.assertEquals(Aggregator.OR, key.getAggregator());
        Assertions.assertEquals(Comparison.EQUALS, key.getComparison());
        Assertions.assertEquals("someField", key.getField());
    }

    @Test
    void isIndependentOfModifierOrder() throws InvalidQueryKeyException {
        final var key = new FilterQueryKey("or:eq:someField");
        final var shuffled = new FilterQueryKey("eq:or:someField");
        Assertions.assertEquals(key, shuffled);
    }

    @Test
    void hasValidAggregatorAssociations() throws InvalidQueryKeyException {
        Assertions.assertEquals(new FilterQueryKey("or:someField").getAggregator(), Aggregator.OR);
        Assertions.assertEquals(new FilterQueryKey("and:someField").getAggregator(), Aggregator.AND);
    }

    @Test
    void hasValidComparisonAssociations() throws InvalidQueryKeyException {
        Assertions.assertEquals(new FilterQueryKey("someField").getComparison(), Comparison.EQUALS);
        Assertions.assertEquals(new FilterQueryKey("eq:someField").getComparison(), Comparison.EQUALS);
        Assertions.assertEquals(new FilterQueryKey("lt:someField").getComparison(), Comparison.LESS_THAN);
        Assertions.assertEquals(new FilterQueryKey("gt:someField").getComparison(), Comparison.GREATER_THAN);
        Assertions.assertEquals(new FilterQueryKey("lte:someField").getComparison(), Comparison.LESS_THAN_EQUALS);
        Assertions.assertEquals(new FilterQueryKey("gte:someField").getComparison(), Comparison.GREATER_THAN_EQUALS);
        Assertions.assertEquals(new FilterQueryKey("in:someField").getComparison(), Comparison.CONTAINS);
    }

    @Test
    void rejectsInvalidKeys() {
        Assertions.assertThrows(InvalidQueryKeyException.class, () -> {
            new FilterQueryKey(":someField");
        });
        Assertions.assertThrows(InvalidQueryKeyException.class, () -> {
            new FilterQueryKey("someField:");
        });
        Assertions.assertThrows(InvalidQueryKeyException.class, () -> {
            new FilterQueryKey("::");
        });
    }

}
