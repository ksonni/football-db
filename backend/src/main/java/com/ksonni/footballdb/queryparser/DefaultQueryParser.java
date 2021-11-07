package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.utils.MathUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultQueryParser<T> implements QueryParser<T> {

    private static final int DEFAULT_PAGE = 0;
    private static final int MAX_PAGE_SIZE = 1000;
    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final String PAGE_SIZE_KEY = "limit";
    private static final String PAGE_KEY = "page";
    private static final String SORT_KEY = "sort";
    private static final String SORT_SEPARATOR = ",";

    private final Map<String, FilterQueryComponentSupplier<T>> fieldMap;

    public DefaultQueryParser(Class<T> objectType) {
        fieldMap = this.populateFieldsMap(objectType);
    }

    public FilterQueryComponentSupplier<T> getQueryComponentSupplier(Field field) {
        var type = field.getType();

        if (Number.class.isAssignableFrom(type)) {
            return (key, value) -> new NumericFilterQueryComponent<>(key, value);
        } else if (String.class.isAssignableFrom(type)) {
            return (key, value) -> new StringFilterQueryComponent<>(key, value);
        } else if (ZonedDateTime.class.isAssignableFrom(type)) {
            return (key, value) -> new DateFilterQueryComponent<T>(key, value);
        }
        return null;
    }

    public final Query<T> parse(String query) throws QueryParseException {
        int page = DEFAULT_PAGE;
        int pageSize = DEFAULT_PAGE_SIZE;
        List<SortQueryKey> sortQueryComponents = new ArrayList<>();
        List<FilterQueryComponent<T>> filterQueryComponents = new ArrayList<>();
        List<NameValuePair> pairs = URLEncodedUtils.parse(query, StandardCharsets.UTF_8);

        for (var pair: pairs) {
            switch (pair.getName()) {
                case PAGE_SIZE_KEY:
                    pageSize = MathUtils.tryParseInt(pair.getValue(), pageSize);
                    pageSize = Math.min(pageSize, MAX_PAGE_SIZE);
                    break;
                case PAGE_KEY:
                    page = MathUtils.tryParseInt(pair.getValue(), page);
                    page = Math.max(page, DEFAULT_PAGE);
                    break;
                case SORT_KEY:
                    sortQueryComponents = parseSortKeys(pair.getValue());
                    break;
                default:
                    FilterQueryComponent<T> queryComponent = parseFilterComponent(pair);
                    if (queryComponent != null) {
                        filterQueryComponents.add(queryComponent);
                    }
                    break;
            }
        }

        return new Query<>(filterQueryComponents, sortQueryComponents, pageSize, page);
    }

    private Map<String, FilterQueryComponentSupplier<T>> populateFieldsMap(Class<T> objectType) {
        var map = new HashMap<String, FilterQueryComponentSupplier<T>>();
        for (var field: objectType.getDeclaredFields()) {
            if (!field.isAnnotationPresent(NonQueryable.class)) {
                FilterQueryComponentSupplier<T> supplier = getQueryComponentSupplier(field);
                map.put(field.getName(), supplier);
            }
        }
        return map;
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

    private FilterQueryComponent<T> parseFilterComponent(NameValuePair pair) throws QueryParseException {
        var key = new FilterQueryKey(pair.getName());
        String value = pair.getValue();
        String field = key.getField();
        if (!fieldMap.containsKey(field)) {
            return null;
        }
        return fieldMap.get(field).get(key, value);
    }

}
