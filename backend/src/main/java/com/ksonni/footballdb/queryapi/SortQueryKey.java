package com.ksonni.footballdb.queryapi;

import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
public final class SortQueryKey extends QueryKey {

    private final boolean descending;

    public SortQueryKey(String key) throws InvalidQueryKeyException {
        super(key);
        descending = parseDescending();
    }

    public Sort getSort() {
        if (descending) {
            return Sort.by(getField()).descending();
        } else {
            return Sort.by(getField());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        SortQueryKey that = (SortQueryKey) o;
        return this.descending == that.descending;
    }

    private boolean parseDescending() {
        for (String modifier: getModifiers()) {
            if (modifier.equals("desc")) {
                return true;
            }
        }
        return false;
    }

}
