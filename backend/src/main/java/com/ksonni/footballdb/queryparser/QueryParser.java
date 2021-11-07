package com.ksonni.footballdb.queryparser;

import java.lang.reflect.Field;

public interface QueryParser<T> {

    FilterQueryComponentSupplier<T> getQueryComponentSupplier(Field field);

    Query<T> parse(String query) throws QueryParseException;

}
