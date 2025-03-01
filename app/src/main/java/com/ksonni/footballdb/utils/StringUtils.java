package com.ksonni.footballdb.utils;

import java.util.UUID;

public final class StringUtils {

    /**
     * General max length limit on most Strings.
     */
    public static final int STRING_MAX_LEN = 40;

    /**
     * Max length limit of country codes.
     */
    public static final int COUNTRY_CODE_MAX_LEN = 4;

    private StringUtils() {
    }

    /**
     * Generates a random UUID.
     *
     * @return UUID.
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

}
