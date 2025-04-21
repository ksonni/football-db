package com.ksonni.footballdb.query;

import com.ksonni.footballdb.generated.ql.QLPagination;
import com.ksonni.footballdb.generated.ql.QLSort;
import com.ksonni.footballdb.generated.ql.QLSortDirection;
import com.ksonni.footballdb.utils.ReflectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DefaultSortParser<SortParams> implements SortParser<SortParams> {
    @Value("${app.max-results-per-page}")
    private Integer maxResults;

    @Value("${app.max-query-components}")
    private Integer maxComponents;

    @Override
    public PageRequest parse(final SortParams sort, final QLPagination pagination) throws SortParseException {
        final var components = parseComponents(sort);
        Optional<Sort> spec = Optional.empty();
        for (var component : components) {
            spec = spec.map(s -> s.and(component.build())).or(() -> Optional.of(component.build()));
        }
        final var page = Math.max(0, pagination.getPage());
        final var size = Math.min(Math.max(0, pagination.getSize()), maxResults);
        return spec.map(orders -> PageRequest.of(page, size, orders)).orElseGet(() -> PageRequest.of(page, size));
    }

    List<SortComponent> parseComponents(final SortParams sort) throws SortParseException {
        final List<SortComponent> components = new ArrayList<>();
        ReflectionUtils.forEachField(sort, (fieldName, value) -> {
            if (components.size() >= maxComponents) {
                throw new SortParseException("Sort has too many components");
            }
            if (!(value instanceof QLSort params)) {
                throw new SortParseException(
                    String.format("Sort has invalid value type: %s", value.getClass().getName())
                );
            }
            components.add(
                new SortComponent(fieldName, params.getPriority(), params.getDirection() == QLSortDirection.DESC)
            );
        });
        return components.stream().sorted(Comparator.comparingInt(SortComponent::order)).toList();
    }
}
