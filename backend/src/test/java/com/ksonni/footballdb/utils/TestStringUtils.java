package com.ksonni.footballdb.utils;

public final class TestStringUtils {

    private TestStringUtils() {
    }

    /**
     * Constructs a string by repeating a sequence.
     *
     * @param sequence The sequence to repeat
     * @param times    Number of times to repeat the sequence
     * @return String with repeated sequence
     */
    public static String repeatedSequence(final String sequence, final int times) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            builder.append(sequence);
        }
        return builder.toString();
    }

    /**
     * Generates an arbitrary String longer than the default max len limit of the system.
     *
     * @return Arbitrary long String.
     */
    public static String longString() {
        return repeatedSequence("X", StringUtils.STRING_MAX_LEN + 1);
    }

}
