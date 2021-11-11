package com.ksonni.footballdb.utils;

public class MathUtils {

    @FunctionalInterface
    public interface Parser<T> {
        T parse(String value);
    }

    public static <T> T tryParse(Parser<T> parser, String str, T defaultVal) {
        try {
            return parser.parse(str);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static <T> T tryParse(Parser<T> parser, String str) {
        try {
            return parser.parse(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
