package com.ksonni.footballdb.utils;

/**
 * Utility similar to BiConsumer that allows throwing a checked exception.
 *
 * @param <T> Consumer value 1
 * @param <U> Consumer value 2
 * @param <E> Exception that can be thrown by the consumer
 */
@FunctionalInterface
public interface ThrowingBiConsumer<T, U, E extends Exception> {
    /**
     * Callback with the values.
     *
     * @param t Consumer value 1
     * @param u Consumer value 2
     * @throws E exception that the consumer can throw.
     */
    void accept(T t, U u) throws E;
}
