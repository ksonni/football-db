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
public final class Query<T> {

    private final List<FilterQueryComponent<T>> filterQueryComponents;
    private final List<SortQueryKey> sortQueryKeys;
    private final int pageSize;
    private final int page;

    public PageRequest constructPageRequest() {
        Sort sort = constructSort();
        return sort != null ?
                PageRequest.of(page, pageSize, sort) :
                PageRequest.of(page, pageSize);
    }

    public Specification<T> constructFilterSpec() {
        Specification<T> spec = null;
        for (var filterComponent: filterQueryComponents) {
            spec = spec != null ? filterComponent.combine(spec) : filterComponent;
        }
        return spec;
    }

    public Sort constructSort() {
        Sort sort = null;
        for (var sortKey: sortQueryKeys) {
            sort = sort != null ? sort.and(sortKey.getSort()) : sortKey.getSort();
        }
        return sort;
    }

}
