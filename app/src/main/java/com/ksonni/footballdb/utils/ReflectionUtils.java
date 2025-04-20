package com.ksonni.footballdb.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * Extracts all instance fields from a Class type.
     *
     * @param type Class type
     * @return instance variables
     * @param <T> type
     */
    public static <T> List<Field> getObjectFields(final Class<T> type) {
        return Arrays.stream(type.getDeclaredFields()).filter(f -> {
            if (Modifier.isStatic(f.getModifiers())) {
                return false;
            }
            return f.trySetAccessible();
        }).toList();
    }

    /**
     * Provides a functional iterator over the field names and values of an object.
     *
     * @param object object to iterate over
     * @param onValue callback with an object's field name and value
     * @param <E> exception that the consumer can throw
     * @throws E passes through exception thrown by the consumer
     */
    public static <E extends Exception> void forEachField(
        final Object object,
        final ThrowingBiConsumer<String, Object, E> onValue
    ) throws E {
        for (final var field: getObjectFields(object.getClass())) {
            final var name = field.getName();
            final Object value;
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                continue;
            }
            if (value == null) {
                continue;
            }
            onValue.accept(name, value);
        }
    }

}
