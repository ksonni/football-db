package com.ksonni.footballdb.users.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ksonni.footballdb.queryparser.components.EnumFilterQueryComponent;
import com.ksonni.footballdb.queryparser.components.InvalidQueryValueException;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import com.ksonni.footballdb.utils.EnumUtils;
import lombok.RequiredArgsConstructor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public enum Role implements EnumUtils.ValueEnum {

    ADMIN("ADMIN"),
    CONTROLLER("CONTROLLER"),
    USER("USER");

    private final String value;

    @JsonCreator
    public static Role of (String str) {
        return (Role) EnumUtils.parseEnum(Role.values(), str);
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    // TODO: Customizable roles
    public List<Permission> getPermissions() {
        switch (this) {
            case CONTROLLER:
                return Arrays.asList(
                    Permission.VIEW_USERS,
                    Permission.MANAGE_CLUBS,
                    Permission.MANAGE_PLAYERS,
                    Permission.MANAGE_LEAGUES
                );
            case ADMIN:
                return Arrays.asList(
                    Permission.VIEW_USERS,
                    Permission.MANAGE_USERS,
                    Permission.MANAGE_CLUBS,
                    Permission.MANAGE_PLAYERS,
                    Permission.MANAGE_LEAGUES
                );
            default:
                return Arrays.asList();
        }
    }

    public static class RoleFilterQueryComponent extends EnumFilterQueryComponent<User, Role> {
        public RoleFilterQueryComponent(FilterQueryKey key, String value) throws InvalidQueryValueException {
            super(key, value);
        }

        @Override
        public Role parseEnum(String value) throws IllegalArgumentException {
            return Role.of(value);
        }
    }

    @Converter(autoApply = true)
    public static class RoleConverter implements AttributeConverter<Role, String> {
        @Override
        public String convertToDatabaseColumn(Role value) {
            if (value == null) {
                return null;
            }
            return value.getValue();
        }

        @Override
        public Role convertToEntityAttribute(String code) {
            return Role.of(code);
        }
    }
    
}
