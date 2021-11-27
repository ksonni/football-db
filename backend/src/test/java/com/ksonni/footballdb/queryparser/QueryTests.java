package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.queryparser.components.*;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import com.ksonni.footballdb.queryparser.keys.SortQueryKey;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class QueryTests {

    public QueryTests() throws QueryParseException {}

    class TestClass {
        Integer someField;
        String otherField;
    }

    private List<FilterQueryComponent<TestClass>> filterComponents = Arrays.asList(
        new NumericFilterQueryComponent<TestClass>(new FilterQueryKey("someField"),"1"),
        new StringFilterQueryComponent<TestClass>(new FilterQueryKey("or:otherField"), "other")
    );

    private List<SortQueryKey> sortKeys = Arrays.asList(
        new SortQueryKey("someField"),
        new SortQueryKey("desc:otherField")
    );

    Query<TestClass> query = new Query<>(filterComponents, sortKeys,
            20, 1);

    @Test
    void constructsPageRequest() {
        PageRequest req = query.constructPageRequest();

        assertEquals(1, req.getPageNumber());
        assertEquals(20, req.getPageSize());
        assertSortMatchesKeys(sortKeys, req.getSort());
    }

    @Test
    void constructsSort() {
        Sort sort = query.constructSort();
        assertSortMatchesKeys(sortKeys, sort);
    }

    @Test
    void constructsSpecification() {
        Specification<TestClass> spec = query.constructFilterSpec();
        assertNotNull(spec); // Unable to test more without JPA context
    }

    private void assertSortMatchesKeys(List<SortQueryKey> keys, Sort sort) {
        List<Sort.Order> orders = sort.get().collect(Collectors.toList());

        assertEquals(keys.size(), orders.size());

        Iterator<SortQueryKey> keysItr = keys.iterator();
        Iterator<Sort.Order> ordersItr = orders.iterator();

        while (keysItr.hasNext() && ordersItr.hasNext()) {
            var key = keysItr.next();
            var order = ordersItr.next();
            assertEquals(key.getField(), order.getProperty());
            assertEquals(key.isDescending(), order.isDescending());
        }
    }

}
