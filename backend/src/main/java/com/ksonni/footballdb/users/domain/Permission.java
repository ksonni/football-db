package com.ksonni.footballdb.users.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ksonni.footballdb.utils.EnumUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Permission implements EnumUtils.ValueEnum, GrantedAuthority {

    VIEW_USERS(Code.VIEW_USERS),
    MANAGE_USERS(Code.MANAGE_USERS),
    MANAGE_PLAYERS(Code.MANAGE_PLAYERS),
    MANAGE_LEAGUES(Code.MANAGE_LEAGUES),
    MANAGE_CLUBS(Code.MANAGE_CLUBS);

    private final String value;

    @JsonCreator
    public static Permission of (String str) {
        return (Permission) EnumUtils.parseEnum(Permission.values(), str);
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String getAuthority() {
        return getValue();
    }

    public static class Code {
        public static final String VIEW_USERS = "VIEW_USERS";
        public static final String MANAGE_USERS = "MANAGE_USERS";
        public static final String MANAGE_PLAYERS = "MANAGE_PLAYERS";
        public static final String MANAGE_LEAGUES = "MANAGE_LEAGUES";
        public static final String MANAGE_CLUBS = "MANAGE_CLUBS";
    }

}
