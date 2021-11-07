package com.ksonni.footballdb.queryparser;

@FunctionalInterface
public interface FilterQueryComponentSupplier<T> {
    FilterQueryComponent<T> get(FilterQueryKey key, String value) throws QueryParseException;
}
