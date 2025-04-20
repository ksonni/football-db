package com.ksonni.footballdb.players.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ksonni.footballdb.utils.EnumUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

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
