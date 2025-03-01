package com.ksonni.footballdb.queryparser;

import org.springframework.data.domain.Page;
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

}
