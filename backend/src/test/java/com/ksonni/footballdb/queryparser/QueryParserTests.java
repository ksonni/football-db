package com.ksonni.footballdb.queryparser;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QueryParserTests {

    class TestClass {
        Integer someField;
        String otherField;
        ZonedDateTime created;
    }

    private QueryParser<TestClass> queryParser = new DefaultQueryParser<>(TestClass.class);

    @Test
    void constructsQuery() throws QueryParseException {
        var queryStr = "lt:someField=23&otherField=hha&sort=desc:someField,otherField&limit=20&page=2" +
        "&created=2021-02-01T20:00:00Z";
        Query<TestClass> query = queryParser.parse(queryStr);

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
    void rejectsInvalidQueries() {
        var queryStr = "lt:someField=23&otherField:=hha&sort=desc:someField,otherField&limit=20&page=2";
        assertThrows(InvalidQueryKeyException.class, () -> queryParser.parse(queryStr));
    }

    @Test
    void picksTheRightDefaults() throws QueryParseException {
       Query<TestClass> query = queryParser.parse("");
        assertEquals(Arrays.asList(), query.getSortQueryKeys());
        assertEquals(Arrays.asList(), query.getFilterQueryComponents());
        assertEquals(0, query.getPage());
        assertEquals(100, query.getPageSize());
    }

    @Test
    void enforcesMaxPageSizeLimits() throws QueryParseException {
        var queryStr = "limit=10000000";
        Query<TestClass> query = queryParser.parse(queryStr);
        assertEquals(1000, query.getPageSize());
    }

    @Test
    void ignoresInvalidPagingValues() throws QueryParseException {
        var queryStr = "limit=asdf&page=sssss";
        Query<TestClass> query = queryParser.parse(queryStr);
        assertEquals(100, query.getPageSize());
        assertEquals(0, query.getPage());
    }

}
