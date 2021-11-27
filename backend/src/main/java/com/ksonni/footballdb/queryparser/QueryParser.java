package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.queryparser.components.FilterQueryComponentSupplier;

import java.lang.reflect.Field;

public interface QueryParser<T> {

    FilterQueryComponentSupplier<T> getQueryComponentSupplier(Field field);

    Query<T> parse(String query) throws QueryParseException;

}
