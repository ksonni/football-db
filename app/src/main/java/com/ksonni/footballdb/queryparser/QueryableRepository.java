package com.ksonni.footballdb.queryparser;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface QueryableRepository<T, ID> extends JpaRepository<T, ID>,
        JpaSpecificationExecutor<T> {

    /**
     * Executes the query on the DB to fetch items.
     *
     * @param query Query object
     * @return Paginated list of entities matching the query
     */
    default Page<T> findAll(Query<T> query) {
        return this.findAll(query.constructFilterSpec(), query.constructPageRequest());
    }

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
