package com.ksonni.footballdb.queryparser.components;

import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;

@FunctionalInterface
public interface FilterQueryComponentSupplier<T> {

    /**
     * Combines the processed key and the string value of a URL query component to construct a FilterQueryComponent.
     *
     * @param key   Parsed FilterQueryKey
     * @param value String value to be parsed
     * @return the parsed component
     * @throws QueryParseException If constructing the component fails
     */
    FilterQueryComponent<T> get(FilterQueryKey key, String value) throws QueryParseException;

}
