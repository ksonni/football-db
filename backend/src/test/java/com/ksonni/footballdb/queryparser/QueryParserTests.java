package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.queryparser.components.DateFilterQueryComponent;
import com.ksonni.footballdb.queryparser.components.NumericFilterQueryComponent;
import com.ksonni.footballdb.queryparser.components.StringFilterQueryComponent;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import com.ksonni.footballdb.queryparser.keys.InvalidQueryKeyException;
import com.ksonni.footballdb.queryparser.keys.SortQueryKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;

public class QueryParserTests {

    private QueryParser<TestClass> queryParser = new DefaultQueryParser<>(TestClass.class);

    @Test
    void constructsQuery() throws QueryParseException {
        final var queryStr = "lt:someField=23&otherField=hha&sort=desc:someField,otherField&limit=20&page=2"
                + "&created=2021-02-01T20:00:00Z";
        final Query<TestClass> query = queryParser.parse(queryStr);

        Assertions.assertEquals(Arrays.asList(
                new NumericFilterQueryComponent(new FilterQueryKey("lt:someField"), "23"),
                new StringFilterQueryComponent(new FilterQueryKey("otherField"), "hha"),
                new DateFilterQueryComponent(new FilterQueryKey("created"), "2021-02-01T20:00:00Z")
        ), query.getFilterQueryComponents());

        Assertions.assertEquals(Arrays.asList(
                new SortQueryKey("desc:someField"),
                new SortQueryKey("otherField")
        ), query.getSortQueryKeys());

        final var expectedPage = 2;
        final var expectedSize = 20;

        Assertions.assertEquals(expectedPage, query.getPage());
        Assertions.assertEquals(expectedSize, query.getPageSize());
    }

    @Test
    void rejectsInvalidQueries() {
        final var queryStr = "lt:someField=23&otherField:=hha&sort=desc:someField,otherField&limit=20&page=2";
        Assertions.assertThrows(InvalidQueryKeyException.class, () -> queryParser.parse(queryStr));
    }

    @Test
    void picksTheRightDefaults() throws QueryParseException {
        final Query<TestClass> query = queryParser.parse("");
        Assertions.assertEquals(Arrays.asList(), query.getSortQueryKeys());
        Assertions.assertEquals(Arrays.asList(), query.getFilterQueryComponents());
        Assertions.assertEquals(0, query.getPage());
        Assertions.assertEquals(DefaultQueryParser.DEFAULT_PAGE_SIZE, query.getPageSize());
    }

    @Test
    void enforcesMaxPageSizeLimits() throws QueryParseException {
        final var queryStr = "limit=10000000";
        final Query<TestClass> query = queryParser.parse(queryStr);
        Assertions.assertEquals(DefaultQueryParser.MAX_PAGE_SIZE, query.getPageSize());
    }

    @Test
    void ignoresInvalidPagingValues() throws QueryParseException {
        final var queryStr = "limit=asdf&page=sssss";
        final Query<TestClass> query = queryParser.parse(queryStr);
        Assertions.assertEquals(DefaultQueryParser.DEFAULT_PAGE_SIZE, query.getPageSize());
        Assertions.assertEquals(0, query.getPage());
    }

    class TestClass {
        private Integer someField;
        private String otherField;
        private ZonedDateTime created;
    }

}
