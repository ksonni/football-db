package com.ksonni.footballdb.queryparser.keys;

import com.ksonni.footballdb.utils.MathUtils;
import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
public class SortQueryKey extends QueryKey {

    private final boolean descending;

    /**
     * Parses keys of a URL query component that specify how certain fields need to be sorted.
     *
     * @param key String key
     * @throws InvalidQueryKeyException if parsing the sort key fails
     */
    public SortQueryKey(final String key) throws InvalidQueryKeyException {
        super(key);
        descending = parseDescending();
    }

    /**
     * Creates Spring data Sort representation of the key.
     *
     * @return Sort object
     */
    public Sort getSort() {
        if (descending) {
            return Sort.by(getField()).descending();
        } else {
            return Sort.by(getField());
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final SortQueryKey that = (SortQueryKey) o;
        return this.descending == that.descending;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = MathUtils.HASHING_PRIME * result + (descending ? 1 : 0);
        return result;
    }

    private boolean parseDescending() {
        for (String modifier : getModifiers()) {
            if (modifier.equals("desc")) {
                return true;
            }
        }
        return false;
    }

}
