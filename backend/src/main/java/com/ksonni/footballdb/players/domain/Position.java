package com.ksonni.footballdb.players.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ksonni.footballdb.queryparser.components.EnumFilterQueryComponent;
import com.ksonni.footballdb.queryparser.components.InvalidQueryValueException;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import com.ksonni.footballdb.utils.EnumUtils;
import lombok.RequiredArgsConstructor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Represents positions on the field players can assume.
 */
@RequiredArgsConstructor
public enum Position implements EnumUtils.ValueEnum {

    /**
     * Goalkeeper.
     */
    GOALKEEPER("GK"),
    /**
     * Striker.
     */
    STRIKER("ST"),

    /**
     * Central midfield.
     */
    CENTRAL_MIDFIELDER("CM"),
    /**
     * Central midfield focused on defending.
     */
    CENTRAL_DEFENSIVE_MIDFIELDER("CDM"),
    /**
     * Central midfield focused on attacking.
     */
    CENTRAL_ATTACKING_MIDFIELDER("CAM"),
    /**
     * Midfield positioned towards the right side of the field.
     */
    RIGHT_MIDFIELDER("RM"),
    /**
     * Midfield positioned towards the left side of the field.
     */
    LEFT_MIDFIELDER("LM"),

    /**
     * Center forward.
     */
    CENTER_FORWARD("CF"),
    /**
     * Center back or Central defender.
     */
    CENTER_BACK("CB"),

    /**
     * Positioned behind the center back on the left side of the field.
     */
    LEFT_BACK("LB"),
    /**
     * Positioned behind the center back on the right side of the field.
     */
    RIGHT_BACK("RB"),

    /**
     * An attacking player on the left extreme of the field.
     */
    LEFT_WING("LW"),
    /**
     * An attacking player on the right extreme of the field.
     */
    RIGHT_WING("RW"),

    /**
     * A defensive player on the left extreme of the field.
     */
    LEFT_WING_BACK("LWB"),
    /**
     * A defensive player on the right extreme of the field.
     */
    RIGHT_WING_BACK("RWB");

    private final String value;

    /**
     * Parses a string to a Position.
     *
     * @param str String position
     * @return parsed Position
     */
    @JsonCreator
    public static Position of(final String str) {
        return (Position) EnumUtils.parseEnum(Position.values(), str);
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static class PositionFilterQueryComponent extends EnumFilterQueryComponent<Player, Position> {
        /**
         * Parsed QueryComponent that can be used to filter players properties by Position.
         *
         * @param key   parsed FilterQueryKey
         * @param value string value of the Position enum
         * @throws InvalidQueryValueException if enum parsing fails
         */
        public PositionFilterQueryComponent(final FilterQueryKey key, final String value)
                throws InvalidQueryValueException {
            super(key, value);
        }

        @Override
        public Position parseEnum(final String value) throws IllegalArgumentException {
            return Position.of(value);
        }
    }

    @Converter(autoApply = true)
    public static class PositionConverter implements AttributeConverter<Position, String> {
        @Override
        public String convertToDatabaseColumn(final Position value) {
            if (value == null) {
                return null;
            }
            return value.getValue();
        }

        @Override
        public Position convertToEntityAttribute(final String code) {
            return Position.of(code);
        }
    }


}
