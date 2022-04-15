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

@RequiredArgsConstructor
public enum AuthMethod implements EnumUtils.ValueEnum {

    /**
     * Username/password based auth.
     */
    PASSWORD("PASSWORD"),

    /**
     * Google Open ID connect auth.
     */
    GOOGLE_OAUTH2("GOOGLE_OAUTH2");

    private final String value;

    /**
     * Parses the string value of an AuthMethod.
     *
     * @param str AuthMethod string
     * @return Parses AuthMethod
     */
    @JsonCreator
    public static AuthMethod of(final String str) {
        return (AuthMethod) EnumUtils.parseEnum(AuthMethod.values(), str);
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static class AuthMethodFilterQueryComponent extends EnumFilterQueryComponent<User, AuthMethod> {
        /**
         * Parsed QueryComponent that can be used to filter users by AuthMethod.
         *
         * @param key   parsed FilterQueryKey
         * @param value string value of the AuthMethod enum
         * @throws InvalidQueryValueException if enum parsing fails
         */
        public AuthMethodFilterQueryComponent(final FilterQueryKey key, final String value)
                throws InvalidQueryValueException {
            super(key, value);
        }

        @Override
        public AuthMethod parseEnum(final String value) throws IllegalArgumentException {
            return AuthMethod.of(value);
        }
    }

    @Converter(autoApply = true)
    public static class AuthMethodConverter implements AttributeConverter<AuthMethod, String> {
        @Override
        public String convertToDatabaseColumn(final AuthMethod value) {
            if (value == null) {
                return null;
            }
            return value.getValue();
        }

        @Override
        public AuthMethod convertToEntityAttribute(final String code) {
            return AuthMethod.of(code);
        }
    }

}
