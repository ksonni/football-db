package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.queryparser.components.DateFilterQueryComponent;
import com.ksonni.footballdb.queryparser.components.FilterQueryComponent;
import com.ksonni.footballdb.queryparser.components.FilterQueryComponentSupplier;
import com.ksonni.footballdb.queryparser.components.NumericFilterQueryComponent;
import com.ksonni.footballdb.queryparser.components.StringFilterQueryComponent;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import com.ksonni.footballdb.queryparser.keys.InvalidQueryKeyException;
import com.ksonni.footballdb.queryparser.keys.SortQueryKey;
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

    /**
     * Default page results will start at.
     */
    public static final int DEFAULT_PAGE = 0;
    /**
     * Max number of results allowed per page.
     */
    public static final int MAX_PAGE_SIZE = 100;
    /**
     * Default number of results per page.
     */
    public static final int DEFAULT_PAGE_SIZE = 25;

    private static final String PAGE_SIZE_KEY = "limit";
    private static final String PAGE_KEY = "page";
    private static final String SORT_KEY = "sort";
    private static final String SORT_SEPARATOR = ",";

    private final Map<String, FilterQueryComponentSupplier<T>> fieldMap;

    /**
     * Constructs a QueryParser for the specified type of entity.
     *
     * @param objectType Class type of the entity to construct queries for
     */
    public DefaultQueryParser(final Class<T> objectType) {
        fieldMap = this.populateFieldsMap(objectType);
    }

    @Override
    public FilterQueryComponentSupplier<T> getQueryComponentSupplier(final Field field) {
        final var type = field.getType();

        if (Number.class.isAssignableFrom(type)) {
            return (key, value) -> new NumericFilterQueryComponent<>(key, value);
        } else if (String.class.isAssignableFrom(type)) {
            return (key, value) -> new StringFilterQueryComponent<>(key, value);
        } else if (ZonedDateTime.class.isAssignableFrom(type)) {
            return (key, value) -> new DateFilterQueryComponent<T>(key, value);
        }

        return null;
    }

    @Override
    public final Query<T> parse(final String query) throws QueryParseException {
        int page = DEFAULT_PAGE;
        int pageSize = DEFAULT_PAGE_SIZE;
        List<SortQueryKey> sortQueryComponents = new ArrayList<>();
        final List<FilterQueryComponent<T>> filterQueryComponents = new ArrayList<>();
        final List<NameValuePair> pairs = URLEncodedUtils.parse(query, StandardCharsets.UTF_8);

        for (var pair : pairs) {
            switch (pair.getName()) {
                case PAGE_SIZE_KEY:
                    pageSize = MathUtils.tryParse(Integer::parseInt, pair.getValue(), pageSize);
                    pageSize = Math.min(pageSize, MAX_PAGE_SIZE);
                    break;
                case PAGE_KEY:
                    page = MathUtils.tryParse(Integer::parseInt, pair.getValue(), page);
                    page = Math.max(page, DEFAULT_PAGE);
                    break;
                case SORT_KEY:
                    sortQueryComponents = parseSortKeys(pair.getValue());
                    break;
                default:
                    final FilterQueryComponent<T> queryComponent = parseFilterComponent(pair);
                    if (queryComponent != null) {
                        filterQueryComponents.add(queryComponent);
                    }
                    break;
            }
        }

        return new Query<>(filterQueryComponents, sortQueryComponents, pageSize, page);
    }

    private Map<String, FilterQueryComponentSupplier<T>> populateFieldsMap(final Class<T> objectType) {
        final var map = new HashMap<String, FilterQueryComponentSupplier<T>>();
        for (var field : objectType.getDeclaredFields()) {
            if (!field.isAnnotationPresent(NonQueryable.class)) {
                final FilterQueryComponentSupplier<T> supplier = getQueryComponentSupplier(field);
                if (supplier != null) {
                    map.put(field.getName(), supplier);
                }
            }
        }
        return map;
    }

    private List<SortQueryKey> parseSortKeys(final String keys) throws InvalidQueryKeyException {
        final List<SortQueryKey> components = new ArrayList<>();
        if (keys == null) {
            return components;
        }
        for (String strComponent : keys.split(SORT_SEPARATOR)) {
            final var key = new SortQueryKey(strComponent);
            if (fieldMap.containsKey(key.getField())) {
                components.add(key);
            }
        }
        return components;
    }

    private FilterQueryComponent<T> parseFilterComponent(final NameValuePair pair) throws QueryParseException {
        final var key = new FilterQueryKey(pair.getName());
        final String value = pair.getValue();
        final String field = key.getField();
        if (!fieldMap.containsKey(field)) {
            return null;
        }
        return fieldMap.get(field).get(key, value);
    }

}
