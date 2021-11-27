package com.ksonni.footballdb.queryparser.components;

import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface FilterQueryComponent<T> extends Specification<T> {

    FilterQueryKey getKey();

    Comparable getValue();

    @SuppressWarnings("unchecked")
    default Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        FilterQueryKey key = getKey();
        Comparable value = getValue();
        String field = key.getField();

        switch (key.getComparison()) {
            case EQUALS:
                return builder.equal(root.get(field), value);
            case CONTAINS:
                return builder.like(root.get(field).as(String.class), "%" + value.toString() + "%");
            case GREATER_THAN:
                return builder.greaterThan(root.get(field), value);
            case LESS_THAN:
                return builder.lessThan(root.get(field), value);
            case GREATER_THAN_EQUALS:
                return builder.greaterThanOrEqualTo(root.get(field), value);
            case LESS_THAN_EQUALS:
                return builder.lessThanOrEqualTo(root.get(field), value);
            default:
                return null;
        }
    }

    default Specification<T> combine(Specification<T> other) {
        switch (getKey().getAggregator()) {
            case AND:
                return other.and(this);
            case OR:
                return other.or(this);
            default:
                return other;
        }
    }

}
