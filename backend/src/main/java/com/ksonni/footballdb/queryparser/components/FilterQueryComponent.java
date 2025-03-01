package com.ksonni.footballdb.queryparser.components;

import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Represents a URL query component to be used for filtering data.
 *
 * @param <T> Type of entity the component will be used to filter.
 */
public interface FilterQueryComponent<T> extends Specification<T> {

    /**
     * Gets the parsed key of a FilterQueryComponent.
     *
     * @return filter query key
     */
    FilterQueryKey getKey();

    /**
     * Gets the parsed value of a FilterQueryComponent.
     *
     * @return filter query key
     */
    Comparable getValue();

    @Override
    @SuppressWarnings("unchecked")
    default Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        final FilterQueryKey key = getKey();
        final Comparable value = getValue();
        final String field = key.getField();

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

    /**
     * Creates a Specification by merging another Specification,
     * using the aggregator specified in the key.
     *
     * @param other Specification to merge with
     * @return Aggregated Specification that can be used by data repositories
     */
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
