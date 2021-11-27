package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import com.ksonni.footballdb.queryparser.keys.Aggregator;
import com.ksonni.footballdb.queryparser.keys.Comparison;
import com.ksonni.footballdb.queryparser.keys.InvalidQueryKeyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilterQueryKeyTests {

    @Test
    void parsesQueryKey() throws InvalidQueryKeyException {
        var key = new FilterQueryKey("or:eq:someField");
        assertEquals(Aggregator.OR, key.getAggregator());
        assertEquals(Comparison.EQUALS, key.getComparison());
        assertEquals("someField", key.getField());
    }

    @Test
    void parsesWithNoModifiers() throws InvalidQueryKeyException {
        var key = new FilterQueryKey("someField");
        assertEquals(Aggregator.AND, key.getAggregator());
        assertEquals(Comparison.EQUALS, key.getComparison());
        assertEquals("someField", key.getField());
    }

    @Test
    void parsesOnlyComparisonModifier() throws InvalidQueryKeyException {
        var key = new FilterQueryKey("lt:someField");
        assertEquals(Aggregator.AND, key.getAggregator());
        assertEquals(Comparison.LESS_THAN, key.getComparison());
        assertEquals("someField", key.getField());
    }

    @Test
    void parsesOnlyAggregatorModifier() throws InvalidQueryKeyException {
        var key = new FilterQueryKey("or:someField");
        assertEquals(Aggregator.OR, key.getAggregator());
        assertEquals(Comparison.EQUALS, key.getComparison());
        assertEquals("someField", key.getField());
    }

    @Test
    void isIndependentOfModifierOrder() throws InvalidQueryKeyException {
        var key = new FilterQueryKey("or:eq:someField");
        var shuffled = new FilterQueryKey("eq:or:someField");
        assertEquals(key, shuffled);
    }

    @Test
    void hasValidAggregatorAssociations() throws InvalidQueryKeyException {
        assertEquals(new FilterQueryKey("or:someField").getAggregator(), Aggregator.OR);
        assertEquals(new FilterQueryKey("and:someField").getAggregator(), Aggregator.AND);
    }

    @Test
    void hasValidComparisonAssociations() throws InvalidQueryKeyException {
        assertEquals(new FilterQueryKey("someField").getComparison(), Comparison.EQUALS);
        assertEquals(new FilterQueryKey("eq:someField").getComparison(), Comparison.EQUALS);
        assertEquals(new FilterQueryKey("lt:someField").getComparison(), Comparison.LESS_THAN);
        assertEquals(new FilterQueryKey("gt:someField").getComparison(), Comparison.GREATER_THAN);
        assertEquals(new FilterQueryKey("lte:someField").getComparison(), Comparison.LESS_THAN_EQUALS);
        assertEquals(new FilterQueryKey("gte:someField").getComparison(), Comparison.GREATER_THAN_EQUALS);
        assertEquals(new FilterQueryKey("in:someField").getComparison(), Comparison.CONTAINS);
    }

    @Test
    void rejectsInvalidKeys() {
        assertThrows(InvalidQueryKeyException.class, () -> {
           new FilterQueryKey(":someField");
        });
        assertThrows(InvalidQueryKeyException.class, () -> {
            new FilterQueryKey("someField:");
        });
        assertThrows(InvalidQueryKeyException.class, () -> {
            new FilterQueryKey("::");
        });
    }

}
