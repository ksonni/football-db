package com.ksonni.footballdb.players.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ksonni.footballdb.utils.EnumUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Working rate of a player in a given area of the game.
 */
@Getter
@RequiredArgsConstructor
public enum WorkRate implements EnumUtils.ValueEnum {
    /**
     * Low working rate.
     */
    LOW(1),

    /**
     * Medium working rate.
     */
    MEDIUM(2),

    /**
     * High working rate.
     */
    HIGH(3);

    private final Integer value;

    /**
     * Parses an integer to WorkRate.
     *
     * @param rate int to parse
     * @return parsed WorkRate
     */
    @JsonCreator
    public static WorkRate of(final Integer rate) {
        return (WorkRate) EnumUtils.parseEnum(WorkRate.values(), rate);
    }

    @JsonValue
    public Integer getValue() {
        return this.value;
    }

    @Converter(autoApply = true)
    public static class WorkRateConverter implements AttributeConverter<WorkRate, Integer> {
        @Override
        public Integer convertToDatabaseColumn(final WorkRate rate) {
            if (rate == null) {
                return null;
            }
            return rate.getValue();
        }

        @Override
        public WorkRate convertToEntityAttribute(final Integer rate) {
            return WorkRate.of(rate);
        }
    }

}
