package com.ksonni.footballdb.query;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface QueryableRepository<T, ID> extends JpaRepository<T, ID>,
        JpaSpecificationExecutor<T> {

    /**
     * Fetches results and wraps them in a PageResult object.
     *
     * @param spec Spring data specification for querying data.
     * @param request Spring data page request to specify sort order and pagination.
     * @return paginated list of entities matching the query.
     */
    default PageResult<T> findAllResults(@Nullable Specification<T> spec, PageRequest request) {
        final var page = this.findAll(spec, request);
        return new PageResult<>(
            page.get().toList(),
            (int) page.getTotalElements(),
            page.getTotalPages(),
            page.getSize()
        );
    }
}
