package com.ksonni.footballdb.queryapi;

import com.ksonni.footballdb.lib.MathUtils;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@EqualsAndHashCode
public final class Query<T> {

    private final List<FilterQueryComponent<T>> filterQueryComponents;
    private final List<SortQueryKey> sortQueryKeys;
    private final int pageSize;
    private final int page;

    @Getter(AccessLevel.NONE)
    private final Map<String, Field> fieldMap;

    private static final int DEFAULT_PAGE = 0;
    private static final int MAX_PAGE_SIZE = 1000;
    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final String PAGE_SIZE_KEY = "limit";
    private static final String PAGE_KEY = "page";
    private static final String SORT_KEY = "sort";
    private static final String SORT_SEPARATOR = ",";

    public Query(URI uri, Class<T> objectType) throws InvalidQueryKeyException, InvalidQueryValueException {
        int page = DEFAULT_PAGE;
        int pageSize = DEFAULT_PAGE_SIZE;
        List<SortQueryKey> sortQueryComponents = new ArrayList<>();
        List<FilterQueryComponent<T>> filterQueryComponents = new ArrayList<>();
        List<NameValuePair> pairs = URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);
        fieldMap = new QueryableFieldsMap<>(objectType);

        for (var pair: pairs) {
            switch (pair.getName()) {
                case PAGE_SIZE_KEY:
                    pageSize = MathUtils.tryParseInt(pair.getValue(), pageSize);
                    break;
                case PAGE_KEY:
                    page = MathUtils.tryParseInt(pair.getValue(), page);
                    break;
                case SORT_KEY:
                    sortQueryComponents = parseSortKeys(pair.getValue());
                    break;
                default:
                    FilterQueryComponent<T> queryComponent = parseFilterComponent(pair.getName(), pair.getValue());
                    if (queryComponent != null) {
                        filterQueryComponents.add(queryComponent);
                    }
                    break;
            }
        }

        this.page = Math.max(page, DEFAULT_PAGE);
        this.pageSize = Math.min(pageSize, MAX_PAGE_SIZE);
        this.sortQueryKeys = sortQueryComponents;
        this.filterQueryComponents = filterQueryComponents;
    }

    public PageRequest constructPageRequest() {
        Sort sort = constructSort();
        return sort != null ?
                PageRequest.of(page, pageSize, sort) :
                PageRequest.of(page, pageSize);
    }

    public Specification<T> constructFilterSpec() {
        Specification<T> spec = null;
        for (var filterComponent: filterQueryComponents) {
            spec = spec != null ? filterComponent.combine(spec) : filterComponent;
        }
        return spec;
    }

    public Sort constructSort() {
        Sort sort = null;
        for (var sortKey: sortQueryKeys) {
            sort = sort != null ? sort.and(sortKey.getSort()) : sortKey.getSort();
        }
        return sort;
    }

    private List<SortQueryKey> parseSortKeys(String keys) throws InvalidQueryKeyException {
        List<SortQueryKey> components = new ArrayList<>();
        if (keys == null) {
            return components;
        }
        for (String strComponent: keys.split(SORT_SEPARATOR)) {
            var key = new SortQueryKey(strComponent);
            if (fieldMap.containsKey(key.getField())) {
                components.add(key);
            }
        }
        return components;
    }

    private FilterQueryComponent<T> parseFilterComponent(String strKey, String value)
            throws InvalidQueryKeyException, InvalidQueryValueException {
        var key = new FilterQueryKey(strKey);
        if (!fieldMap.containsKey(key.getField())) { return null; }

        Class type = fieldMap.get(key.getField()).getType();

        if (Number.class.isAssignableFrom(type)) {
            return new NumericFilterQueryComponent<>(key, value);
        } else if (String.class.isAssignableFrom(type)) {
            return new StringFilterQueryComponent<>(key, value);
        } else if (ZonedDateTime.class.isAssignableFrom(type)) {
            return new DateFilterQueryComponent<T>(key, value);
        }
        return null;
    }

}
