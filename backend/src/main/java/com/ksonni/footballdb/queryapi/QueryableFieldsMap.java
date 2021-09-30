package com.ksonni.footballdb.queryapi;

import java.lang.reflect.Field;
import java.util.HashMap;

public class QueryableFieldsMap<T> extends HashMap<String, Field> {

    public QueryableFieldsMap(Class<T> objectType) {
        for (var field: objectType.getDeclaredFields()) {
            if (!field.isAnnotationPresent(NonQueryable.class)) {
                this.put(field.getName(), field);
            }
        }
    }

}
