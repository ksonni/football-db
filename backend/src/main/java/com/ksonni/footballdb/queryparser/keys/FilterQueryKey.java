package com.ksonni.footballdb.queryparser.keys;

import com.ksonni.footballdb.utils.MathUtils;
import lombok.Getter;

@Getter
public class FilterQueryKey extends QueryKey {

    private final Aggregator aggregator;
    private final Comparison comparison;

    /**
     * Parses the key of a URL query component, to be used for filtering.
     *
     * @param key String key
     * @throws InvalidQueryKeyException if parsing the key fails
     */
    public FilterQueryKey(final String key) throws InvalidQueryKeyException {
        super(key);
        aggregator = parseAggregator();
        comparison = parseComparison();
    }

    private Aggregator parseAggregator() {
        for (String modifier : getModifiers()) {
            switch (modifier) {
                case "or":
                    return Aggregator.OR;
                case "and":
                    return Aggregator.AND;
                default:
                    break;
            }
        }
        return Aggregator.AND;
    }

    private Comparison parseComparison() {
        for (String modifier : getModifiers()) {
            switch (modifier) {
                case "eq":
                    return Comparison.EQUALS;
                case "lt":
                    return Comparison.LESS_THAN;
                case "gt":
                    return Comparison.GREATER_THAN;
                case "lte":
                    return Comparison.LESS_THAN_EQUALS;
                case "gte":
                    return Comparison.GREATER_THAN_EQUALS;
                case "in":
                    return Comparison.CONTAINS;
                default:
                    break;
            }
        }
        return Comparison.EQUALS;
    }

    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final FilterQueryKey that = (FilterQueryKey) o;
        return aggregator == that.aggregator
                && comparison == that.comparison;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = MathUtils.HASHING_PRIME * result + aggregator.hashCode();
        result = MathUtils.HASHING_PRIME * result + comparison.hashCode();
        return result;
    }
}
