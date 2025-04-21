package com.ksonni.footballdb.query;

import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface FilterParser<Entity, Filter> {
    /**
     * Parses a GraphQL filter using reflection and constructs Spring data Specification.
     *
     * @param filter filter of a custom type
     * @return Spring Data specification
     * @throws FilterParseException thrown if parsing of the sort type fails
     */
    Optional<Specification<Entity>> parse(Filter filter) throws FilterParseException;

    /**
     * Registers a ValueDecoder that the parser uses to covert GraphQL value
     * of a certain type to its database representation.
     *
     * @param type type of the GraphQL value
     * @param decoder decoder to convert the value to its database representation
     * @param <T> GraphQL value type
     * @param <V> Database representation of the value
     */
    <T, V> void registerDecoder(Class<T> type, ValueDecoder<T, V> decoder);

    /**
     * Ensures ValueDecoders have been registered for all fields of the root filter type.
     *
     * @throws IllegalStateException thrown if ValueDecoders are missing
     * @param type type of the root filter
     */
    void assertDecodable(Class<Filter> type);
}
