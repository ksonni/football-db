package com.ksonni.footballdb.qlquery;

import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;

@FunctionalInterface
public interface ValueDecoder<T, V> {
    /**
     * Converts a GraphQL value to its database representation.
     *
     * @param value GraphQL value
     * @return database representation
     */
    Comparable<V> getValue(T value);
}

class PrimitiveValueDecoder<V extends Comparable<V>> implements ValueDecoder<V, V> {
    @Override
    public Comparable<V> getValue(final V value) {
        return value;
    }
}

class DateTimeDecoder implements ValueDecoder<ZonedDateTime, ChronoZonedDateTime<?>> {
    @Override
    public Comparable<ChronoZonedDateTime<?>> getValue(final ZonedDateTime value) {
        return value;
    }
}
