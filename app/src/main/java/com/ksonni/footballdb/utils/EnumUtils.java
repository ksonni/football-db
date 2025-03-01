package com.ksonni.footballdb.utils;

import java.util.stream.Stream;

public final class EnumUtils {

    private EnumUtils() {
    }

    /**
     * Constructs an enum by parsing a Comparable.
     *
     * @param values List of values the enum can assume
     * @param value  The value to parse
     * @return The parsed enum
     * @throws IllegalArgumentException Thrown if the comparable is not assignable to the enum
     */
    public static ValueEnum parseEnum(final ValueEnum[] values, final Comparable value)
            throws IllegalArgumentException {
        if (value == null) {
            return null;
        }
        return Stream.of(values)
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public interface ValueEnum {

        /**
         * Gets the Comparable value for an enum.
         *
         * @return Comparable value for the enum.
         */
        Comparable getValue();

    }

}
