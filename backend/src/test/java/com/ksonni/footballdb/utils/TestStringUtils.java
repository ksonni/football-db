package com.ksonni.footballdb.utils;

public class TestStringUtils {

    public static String repeatedSequence(String sequence, int times) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            builder.append(sequence);
        }
        return builder.toString();
    }

    public static String longString() {
        return repeatedSequence("X", 41);
    }

}
