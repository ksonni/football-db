package com.ksonni.footballdb.queryparser.keys;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@EqualsAndHashCode
public class QueryKey {

    private static final String SEPARATOR = ":";
    private final String field;
    private final List<String> modifiers;

    /**
     * Parses the key of a URL query component.
     *
     * @param key String key
     * @throws InvalidQueryKeyException if parsing a key fails
     */
    public QueryKey(final String key) throws InvalidQueryKeyException {
        if (!isKeyValid(key)) {
            throw new InvalidQueryKeyException(key);
        }
        final String[] components = key.split(SEPARATOR);
        modifiers = extractModifiers(components);
        field = components[components.length - 1];
    }

    private boolean isKeyValid(final String key) {
        return key.length() > 0 && !key.startsWith(SEPARATOR)
                && !key.endsWith(SEPARATOR);
    }

    private List<String> extractModifiers(final String[] components) {
        final List<String> mods = new ArrayList<>();
        for (int i = 0; i < components.length - 1; i++) {
            mods.add(components[i]);
        }
        Collections.sort(mods);
        return mods;
    }

}
