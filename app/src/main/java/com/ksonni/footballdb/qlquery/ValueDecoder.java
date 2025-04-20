package com.ksonni.footballdb.qlquery;

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
