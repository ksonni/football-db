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

    /**
     * Administrator role with all permissions.
     */
    ADMIN("ADMIN"),

    /**
     * Role that can manage all resources but not other users.
     */
    CONTROLLER("CONTROLLER"),

    /**
     * User of the database with read only access.
     */
    USER("USER");

    private final String value;

    /**
     * Parses the string value of a Permission.
     *
     * @param str Permission string
     * @return Parses Permission
     */
    @JsonCreator
    public static Role of(final String str) {
        return (Role) EnumUtils.parseEnum(Role.values(), str);
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    // TODO: Customizable roles

    /**
     * List of permissions associated with the user.
     *
     * @return permissions list
     */
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
        /**
         * Parsed QueryComponent that can be used to filter users by Role.
         *
         * @param key   parsed FilterQueryKey
         * @param value string value of the Role enum
         * @throws InvalidQueryValueException if enum parsing fails
         */
        public RoleFilterQueryComponent(final FilterQueryKey key, final String value)
                throws InvalidQueryValueException {
            super(key, value);
        }

        @Override
        public Role parseEnum(final String value) throws IllegalArgumentException {
            return Role.of(value);
        }
    }

    @Converter(autoApply = true)
    public static class RoleConverter implements AttributeConverter<Role, String> {
        @Override
        public String convertToDatabaseColumn(final Role value) {
            if (value == null) {
                return null;
            }
            return value.getValue();
        }

        @Override
        public Role convertToEntityAttribute(final String code) {
            return Role.of(code);
        }
    }

}
