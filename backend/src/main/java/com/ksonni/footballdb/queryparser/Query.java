package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.queryparser.components.FilterQueryComponent;
import com.ksonni.footballdb.queryparser.keys.SortQueryKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Query<T> {

    private final List<FilterQueryComponent<T>> filterQueryComponents;
    private final List<SortQueryKey> sortQueryKeys;
    private final int pageSize;
    private final int page;

    /**
     * Constructs a PageRequest from the query.
     *
     * @return PageRequest that can be processed by data repositories
     */
    public PageRequest constructPageRequest() {
        final Sort sort = constructSort();
        return sort != null ? PageRequest.of(page, pageSize, sort)
                : PageRequest.of(page, pageSize);
    }

    /**
     * Combines FilterQueryComponent to construct a filter Specification.
     *
     * @return Specification that can be processed by data repositories
     */
    public Specification<T> constructFilterSpec() {
        Specification<T> spec = null;
        for (var filterComponent : filterQueryComponents) {
            spec = spec != null ? filterComponent.combine(spec) : filterComponent;
        }
        return spec;
    }

    /**
     * Combines SortQueryKeys to construct a Sort.
     *
     * @return Sort spec that can be processed by data repositories
     */
    public Sort constructSort() {
        Sort sort = null;
        for (var sortKey : sortQueryKeys) {
            sort = sort != null ? sort.and(sortKey.getSort()) : sortKey.getSort();
        }
        return sort;
    }

}
