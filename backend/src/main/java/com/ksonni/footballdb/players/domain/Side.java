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

@RequiredArgsConstructor
public enum Side implements EnumUtils.ValueEnum {
    LEFT("LEFT"), RIGHT("RIGHT");

    private final String value;

    @JsonCreator
    public static Side of (String str) {
        return (Side) EnumUtils.parseEnum(Side.values(), str);
    }

    @JsonValue
    public String getValue() {
        return value;
    }


    public static class SideFilterQueryComponent extends EnumFilterQueryComponent<Player, Side> {
        public SideFilterQueryComponent(FilterQueryKey key, String value) throws InvalidQueryValueException {
            super(key, value);
        }

        @Override
        public Side parseEnum(String value) throws IllegalArgumentException {
            return Side.of(value);
        }
    }

    @Converter(autoApply = true)
    public static class SideConverter implements AttributeConverter<Side, String> {
        @Override
        public String convertToDatabaseColumn(Side category) {
            if (category == null) {
                return null;
            }
            return category.getValue();
        }

        @Override
        public Side convertToEntityAttribute(String code) {
            return Side.of(code);
        }
    }


}
