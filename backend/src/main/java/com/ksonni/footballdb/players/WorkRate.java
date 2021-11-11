package com.ksonni.footballdb.players;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ksonni.footballdb.queryparser.EnumFilterQueryComponent;
import com.ksonni.footballdb.queryparser.FilterQueryKey;
import com.ksonni.footballdb.queryparser.InvalidQueryValueException;
import com.ksonni.footballdb.utils.EnumUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Getter
@RequiredArgsConstructor
public enum WorkRate implements EnumUtils.ValueEnum {
    LOW(1), MEDIUM(2), HIGH(3);

    private final Integer value;

    @JsonCreator
    public static WorkRate of (Integer rate) {
        return (WorkRate) EnumUtils.parseEnum(WorkRate.values(), rate);
    }

    @JsonValue
    public Integer getValue() {
        return this.value;
    }
}

class WorkRateFilterQueryComponent extends EnumFilterQueryComponent<Player, WorkRate> {
    WorkRateFilterQueryComponent(FilterQueryKey key, String value) throws InvalidQueryValueException {
        super(key, value);
    }

    @Override
    public WorkRate parseEnum(String value) throws IllegalArgumentException {
        try {
            return WorkRate.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
    }
}

@Converter(autoApply = true)
class WorkRateConverter implements AttributeConverter<WorkRate, Integer> {
    @Override
    public Integer convertToDatabaseColumn(WorkRate rate) {
        if (rate == null) {
            return null;
        }
        return rate.getValue();
    }

    @Override
    public WorkRate convertToEntityAttribute(Integer rate) {
        return WorkRate.of(rate);
    }
}
