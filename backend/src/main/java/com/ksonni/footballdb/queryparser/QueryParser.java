package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.queryparser.components.FilterQueryComponentSupplier;

import java.lang.reflect.Field;

public interface QueryParser<T> {

    /**
     * Returns a supplier that can parse a component of the URL query.
     *
     * @param field Any field in the generic type T of the QueryParser
     * @return Supplier to parse query components
     */
    FilterQueryComponentSupplier<T> getQueryComponentSupplier(Field field);

    /**
     * Constructs a Query from a URL query string.
     *
     * @param query URL query
     * @return Parsed Query object
     * @throws QueryParseException if parsing of the query fails
     */
    Query<T> parse(String query) throws QueryParseException;

}
