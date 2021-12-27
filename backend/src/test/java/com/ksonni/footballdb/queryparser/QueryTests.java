package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.queryparser.components.FilterQueryComponent;
import com.ksonni.footballdb.queryparser.components.NumericFilterQueryComponent;
import com.ksonni.footballdb.queryparser.components.StringFilterQueryComponent;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import com.ksonni.footballdb.queryparser.keys.SortQueryKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class QueryTests {

    private static final int TESTING_PAGE_SIZE = 20;

    private final List<FilterQueryComponent<TestClass>> filterComponents = Arrays.asList(
            new NumericFilterQueryComponent<TestClass>(new FilterQueryKey("someField"), "1"),
            new StringFilterQueryComponent<TestClass>(new FilterQueryKey("or:otherField"), "other")
    );
    private final List<SortQueryKey> sortKeys = Arrays.asList(
            new SortQueryKey("someField"),
            new SortQueryKey("desc:otherField")
    );
    private final Query<TestClass> query = new Query<>(filterComponents, sortKeys,
            TESTING_PAGE_SIZE, 1);

    QueryTests() throws QueryParseException {
    }

    @Test
    void constructsPageRequest() {
        final PageRequest req = query.constructPageRequest();

        Assertions.assertEquals(1, req.getPageNumber());
        Assertions.assertEquals(TESTING_PAGE_SIZE, req.getPageSize());
        assertSortMatchesKeys(sortKeys, req.getSort());
    }

    @Test
    void constructsSort() {
        final Sort sort = query.constructSort();
        assertSortMatchesKeys(sortKeys, sort);
    }

    @Test
    void constructsSpecification() {
        final Specification<TestClass> spec = query.constructFilterSpec();
        Assertions.assertNotNull(spec); // Unable to test more without JPA context
    }

    private void assertSortMatchesKeys(final List<SortQueryKey> keys, final Sort sort) {
        final List<Sort.Order> orders = sort.get().collect(Collectors.toList());

        Assertions.assertEquals(keys.size(), orders.size());

        final Iterator<SortQueryKey> keysItr = keys.iterator();
        final Iterator<Sort.Order> ordersItr = orders.iterator();

        while (keysItr.hasNext() && ordersItr.hasNext()) {
            final var key = keysItr.next();
            final var order = ordersItr.next();
            Assertions.assertEquals(key.getField(), order.getProperty());
            Assertions.assertEquals(key.isDescending(), order.isDescending());
        }
    }

    class TestClass {
        private Integer someField;
        private String otherField;
    }

}
