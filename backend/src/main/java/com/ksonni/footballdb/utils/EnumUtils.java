package com.ksonni.footballdb.utils;

import java.util.stream.Stream;

public class EnumUtils {

    public interface ValueEnum {
        Comparable getValue();
    }

    public static ValueEnum parseEnum(ValueEnum[] values, Comparable value) throws IllegalArgumentException {
        if (value == null) {
            return null;
        }
        return Stream.of(values)
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
