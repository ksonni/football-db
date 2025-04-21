package com.ksonni.footballdb.query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

@SuppressWarnings("unchecked")
record FilterComponent<Entity>(String key, Comparison comparison, Comparable value) implements Specification<Entity> {
    enum Comparison {
        EQUALS, NOT_EQUALS, LESS_THAN, GREATER_THAN, LESS_THAN_EQUALS, GREATER_THAN_EQUALS, CONTAINS,
    }

    @Override
    public Predicate toPredicate(
        final Root<Entity> root,
        final CriteriaQuery<?> query,
        final CriteriaBuilder builder
    ) {
        return switch (this.comparison) {
            case EQUALS -> builder.equal(root.get(key), value);
            case NOT_EQUALS -> builder.notEqual(root.get(key), value);
            case LESS_THAN -> builder.lessThan(root.get(key), value);
            case GREATER_THAN -> builder.greaterThan(root.get(key), value);
            case LESS_THAN_EQUALS -> builder.lessThanOrEqualTo(root.get(key), value);
            case GREATER_THAN_EQUALS -> builder.greaterThanOrEqualTo(root.get(key), value);
            case CONTAINS -> builder.like(root.get(key).as(String.class), "%" + value.toString() + "%");
        };
    }
}
