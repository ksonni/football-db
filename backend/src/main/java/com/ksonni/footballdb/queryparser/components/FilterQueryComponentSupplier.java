package com.ksonni.footballdb.queryparser.components;

import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;

@FunctionalInterface
public interface FilterQueryComponentSupplier<T> {
    FilterQueryComponent<T> get(FilterQueryKey key, String value) throws QueryParseException;
}
