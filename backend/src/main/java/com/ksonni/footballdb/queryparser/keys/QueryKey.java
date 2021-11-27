package com.ksonni.footballdb.queryparser.keys;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@EqualsAndHashCode
public class QueryKey {

    private final String field;
    private final List<String> modifiers;

    private static final String SEPARATOR = ":";

    public QueryKey(String key) throws InvalidQueryKeyException {
        if (!isKeyValid(key)) {
            throw new InvalidQueryKeyException(key);
        }
        String[] components = key.split(SEPARATOR);
        modifiers = extractModifiers(components);
        field = components[components.length - 1];
    }

    private boolean isKeyValid(String key) {
        return key.length() > 0 && !key.startsWith(SEPARATOR) &&
                !key.endsWith(SEPARATOR);
    }

    private List<String> extractModifiers(String[] components) {
        List<String> modifiers = new ArrayList<>();
        for (int i = 0; i < components.length - 1; i++) {
            modifiers.add(components[i]);
        }
        Collections.sort(modifiers);
        return modifiers;
    }

}
