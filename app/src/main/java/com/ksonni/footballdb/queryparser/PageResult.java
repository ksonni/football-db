package com.ksonni.footballdb.queryparser;

import java.util.List;

public record PageResult<T>(
    List<T> content,
    Integer totalElements,
    Integer totalPages,
    Integer size
) {
}
