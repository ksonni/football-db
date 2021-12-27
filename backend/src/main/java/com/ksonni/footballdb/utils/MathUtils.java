package com.ksonni.footballdb.utils;

public final class MathUtils {

    /**
     * A prime number conventionally used as a multiplier for generating hash codes.
     */
    public static final int HASHING_PRIME = 31;

    /**
     * Upper limit for percentages.
     */
    public static final int MAX_PERCENT = 100;

    private MathUtils() {
    }

    /**
     * Parses a string and returns a default value on failure.
     *
     * @param parser     Lambda to use for parsing
     * @param str        String to parse
     * @param defaultVal Value to return on failure
     * @param <T>        Return type of the lambda
     * @return The parsed value
     */
    public static <T> T tryParse(final Parser<T> parser, final String str, final T defaultVal) {
        try {
            return parser.parse(str);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * Parses a value and returns null on failure.
     *
     * @param parser Lambda to use for parsing
     * @param str    String to parse
     * @param <T>    Return type of the lambda
     * @return The parsed value
     */
    public static <T> T tryParse(final Parser<T> parser, final String str) {
        try {
            return parser.parse(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @FunctionalInterface
    public interface Parser<T> {
        /**
         * Parses a String to any required type.
         *
         * @param value String to parse
         * @return The parsed value
         */
        T parse(String value);
    }

}
