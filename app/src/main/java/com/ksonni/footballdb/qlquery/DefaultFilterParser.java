package com.ksonni.footballdb.qlquery;

import com.ksonni.footballdb.utils.ReflectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.*;

public class DefaultFilterParser<Entity, Filter> implements FilterParser<Entity, Filter> {

    @Value("${app.max-query-components}")
    private Integer maxComponents;

    private final Map<Class<?>, ValueDecoder<?, ?>> decoders = new HashMap<>();

    /**
     * Constructs a parser registering ValueDecoders for primitive Java types.
     */
    public DefaultFilterParser() {
        registerDecoder(Byte.class, new PrimitiveValueDecoder<>());
        registerDecoder(Short.class, new PrimitiveValueDecoder<>());
        registerDecoder(Integer.class, new PrimitiveValueDecoder<>());
        registerDecoder(Long.class, new PrimitiveValueDecoder<>());
        registerDecoder(Float.class, new PrimitiveValueDecoder<>());
        registerDecoder(Double.class, new PrimitiveValueDecoder<>());
        registerDecoder(String.class, new PrimitiveValueDecoder<>());
        registerDecoder(ZonedDateTime.class, new DateTimeDecoder());
    }

    @Override
    public Optional<Specification<Entity>> parse(final Filter filter) throws FilterParseException {
        final var components = parseComponents(filter);
        Optional<Specification<Entity>> spec = Optional.empty();
        for (var component : components) {
            spec = spec.map(s -> s.and(component)).or(() -> Optional.of(component));
        }
        return spec;
    }

    @Override
    public <T, V> void registerDecoder(final Class<T> type, final ValueDecoder<T, V> decoder) {
        decoders.put(type, decoder);
    }

    @Override
    public void assertDecodable(final Class<Filter> type) {
        final var missing = new HashSet<Class<?>>();
        for (var field : ReflectionUtils.getObjectFields(type)) {
            final var filterFields = ReflectionUtils.getObjectFields(field.getType());
            final var noDecoders = filterFields.stream().map(Field::getType)
                .filter(fType -> !decoders.containsKey(fType))
                .toList();
            missing.addAll(noDecoders);
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                String.format("%s has missing value decoders for types %s", type.getName(), missing)
            );
        }
    }

    List<FilterComponent<Entity>> parseComponents(final Filter filter) throws FilterParseException {
        final List<FilterComponent<Entity>> components = new ArrayList<>();
        ReflectionUtils.forEachField(filter, (fieldName, value) -> {
            if (components.size() >= maxComponents) {
                throw new FilterParseException("Filter has too many components");
            }
            components.addAll(parseFilter(fieldName, value));
        });
        return components;
    }

    private List<FilterComponent<Entity>> parseFilter(
        final String key, final Object filter
    ) throws FilterParseException {
        final List<FilterComponent<Entity>> components = new ArrayList<>();

        ReflectionUtils.forEachField(filter, (fieldName, value) -> {
            final var dbValue = decodeValue(value, value.getClass());
            final FilterComponent.Comparison comparison = switch (fieldName) {
                case "eq" -> FilterComponent.Comparison.EQUALS;
                case "ne" -> FilterComponent.Comparison.NOT_EQUALS;
                case "lt" -> FilterComponent.Comparison.LESS_THAN;
                case "gt" -> FilterComponent.Comparison.GREATER_THAN;
                case "lte" -> FilterComponent.Comparison.LESS_THAN_EQUALS;
                case "gte" -> FilterComponent.Comparison.GREATER_THAN_EQUALS;
                case "contains" -> FilterComponent.Comparison.CONTAINS;
                default ->
                    throw new FilterParseException(String.format("Filter has unsupported comparison: %s", fieldName));
            };
            components.add(new FilterComponent<>(key, comparison, dbValue));
        });

        return components;
    }

    @SuppressWarnings("unchecked")
    private <T> Comparable<?> decodeValue(final Object value, final Class<T> type) throws FilterParseException {
        if (!decoders.containsKey(type)) {
            throw new FilterParseException(String.format("No decoders registered for type : %s", type.getName()));
        }
        final var decoder = (ValueDecoder<T, ?>) decoders.get(type);
        final var typedValue = type.cast(value);
        return decoder.getValue(typedValue);
    }
}
