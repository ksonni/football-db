package com.ksonni.footballdb.queryapi;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QueryTests {

    class TestClass {
        Integer someField;
        String otherField;
        ZonedDateTime created;
    }

    @Test
    void constructsQuery() throws URISyntaxException, InvalidQueryKeyException, InvalidQueryValueException {
        var queryStr = "lt:someField=23&otherField=hha&sort=desc:someField,otherField&limit=20&page=2" +
        "&created=2021-02-01T20:00:00Z";
        var query = new Query<>(queryStr, TestClass.class);

        assertEquals(Arrays.asList(
            new NumericFilterQueryComponent(new FilterQueryKey("lt:someField"), "23"),
            new StringFilterQueryComponent(new FilterQueryKey("otherField"), "hha"),
            new DateFilterQueryComponent(new FilterQueryKey("created"), "2021-02-01T20:00:00Z")
        ), query.getFilterQueryComponents());

        assertEquals(Arrays.asList(
            new SortQueryKey("desc:someField"),
            new SortQueryKey("otherField")
        ), query.getSortQueryKeys());

        assertEquals(2, query.getPage());
        assertEquals(20, query.getPageSize());
    }

    @Test
    void rejectsInvalidQueries() throws URISyntaxException  {
        var queryStr = "lt:someField=23&otherField:=hha&sort=desc:someField,otherField&limit=20&page=2";
        assertThrows(InvalidQueryKeyException.class, () -> new Query<>(queryStr, TestClass.class));
    }

    @Test
    void picksTheRightDefaults() throws URISyntaxException, InvalidQueryKeyException, InvalidQueryValueException {
       var query = new Query<>("", TestClass.class);
        assertEquals(Arrays.asList(), query.getSortQueryKeys());
        assertEquals(Arrays.asList(), query.getFilterQueryComponents());
        assertEquals(0, query.getPage());
        assertEquals(100, query.getPageSize());
    }

    @Test
    void enforcesMaxPageSizeLimits() throws URISyntaxException, InvalidQueryKeyException, InvalidQueryValueException {
        var queryStr = "limit=10000000";
        var query = new Query<>(queryStr, TestClass.class);
        assertEquals(1000, query.getPageSize());
    }

    @Test
    void ignoresInvalidPagingValues() throws URISyntaxException, InvalidQueryKeyException, InvalidQueryValueException {
        var queryStr = "limit=asdf&page=sssss";
        var query = new Query<>(queryStr, TestClass.class);
        assertEquals(100, query.getPageSize());
        assertEquals(0, query.getPage());
    }

}
