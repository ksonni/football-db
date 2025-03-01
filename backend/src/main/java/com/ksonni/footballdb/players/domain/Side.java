package com.ksonni.footballdb.players.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ksonni.footballdb.queryparser.components.EnumFilterQueryComponent;
import com.ksonni.footballdb.queryparser.components.InvalidQueryValueException;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import com.ksonni.footballdb.utils.EnumUtils;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Represents a relative direction.
 */
@RequiredArgsConstructor
public enum Side implements EnumUtils.ValueEnum {
    /**
     * Left relative direction.
     */
    LEFT("LEFT"),

    /**
     * Right relative declaration.
     */
    RIGHT("RIGHT");

    private final String value;

    /**
     * Parses a string to a Side.
     *
     * @param str String side
     * @return parsed Side
     */
    @JsonCreator
    public static Side of(final String str) {
        return (Side) EnumUtils.parseEnum(Side.values(), str);
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static class SideFilterQueryComponent extends EnumFilterQueryComponent<Player, Side> {
        /**
         * Parsed QueryComponent that can be used to filter players properties by Side.
         *
         * @param key   parsed FilterQueryKey
         * @param value string value of the Side enum
         * @throws InvalidQueryValueException if enum parsing fails
         */
        public SideFilterQueryComponent(final FilterQueryKey key, final String value)
                throws InvalidQueryValueException {
            super(key, value);
        }

        @Override
        public Side parseEnum(final String value) throws IllegalArgumentException {
            return Side.of(value);
        }
    }

    @Converter(autoApply = true)
    public static class SideConverter implements AttributeConverter<Side, String> {
        @Override
        public String convertToDatabaseColumn(final Side value) {
            if (value == null) {
                return null;
            }
            return value.getValue();
        }

        @Override
        public Side convertToEntityAttribute(final String code) {
            return Side.of(code);
        }
    }


}
