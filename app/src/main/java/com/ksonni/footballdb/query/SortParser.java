package com.ksonni.footballdb.query;

import com.ksonni.footballdb.generated.ql.QLPagination;
import org.springframework.data.domain.PageRequest;

public interface SortParser<Sort> {
    /**
     * Parses a GraphQL sort type using reflection and constructs Spring data PageRequest.
     *
     * @param sort GraphQL sort object of a certain type
     * @param pagination GraphQL pagination object
     * @return Spring Data Page request
     * @throws SortParseException if parsing of the sort type fails
     */
    PageRequest parse(Sort sort, QLPagination pagination) throws SortParseException;
}
