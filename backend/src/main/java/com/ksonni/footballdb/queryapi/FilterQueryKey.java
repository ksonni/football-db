package com.ksonni.footballdb.queryapi;

import lombok.Getter;

@Getter
public class FilterQueryKey extends QueryKey {

    private final Aggregator aggregator;
    private final Comparison comparison;

    public FilterQueryKey(String key) throws InvalidQueryKeyException {
        super(key);
        aggregator = parseAggregator();
        comparison = parseComparison();
    }

    private Aggregator parseAggregator() {
        for (String modifier: getModifiers()) {
            switch(modifier) {
                case "or":
                    return Aggregator.OR;
                case "and":
                    return Aggregator.AND;
            }
        }
        return Aggregator.AND;
    }

    private Comparison parseComparison() {
        for (String modifier: getModifiers()) {
            switch (modifier){
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
            }
        }
        return Comparison.EQUALS;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        FilterQueryKey that = (FilterQueryKey) o;
        return aggregator == that.aggregator &&
                comparison == that.comparison;
    }

}
