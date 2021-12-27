package com.ksonni.footballdb.users.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ksonni.footballdb.utils.EnumUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Permission implements EnumUtils.ValueEnum, GrantedAuthority {
    /**
     * Permission to view all users.
     */
    VIEW_USERS(Code.VIEW_USERS),

    /**
     * Permission to manage users.
     */
    MANAGE_USERS(Code.MANAGE_USERS),

    /**
     * Permission to manage players.
     */
    MANAGE_PLAYERS(Code.MANAGE_PLAYERS),

    /**
     * Permission to manage leagues.
     */
    MANAGE_LEAGUES(Code.MANAGE_LEAGUES),

    /**
     * Permission to manage clubs.
     */
    MANAGE_CLUBS(Code.MANAGE_CLUBS);

    private final String value;

    /**
     * Parses a string to Permission.
     *
     * @param str String permission
     * @return Parsed Permission
     */
    @JsonCreator
    public static Permission of(final String str) {
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

    /**
     * Simple permissions that can be used with @RolesAllowed.
     */
    public static class Code {
        /**
         * VIEW_USERS permission String value.
         */
        public static final String VIEW_USERS = "VIEW_USERS";

        /**
         * MANAGE_USERS permission String value.
         */
        public static final String MANAGE_USERS = "MANAGE_USERS";

        /**
         * MANAGE_PLAYERS permission String value.
         */
        public static final String MANAGE_PLAYERS = "MANAGE_PLAYERS";

        /**
         * MANAGE_LEAGUES permission String value.
         */
        public static final String MANAGE_LEAGUES = "MANAGE_LEAGUES";

        /**
         * MANAGE_CLUBS permission String value.
         */
        public static final String MANAGE_CLUBS = "MANAGE_CLUBS";
    }

    /**
     * Compound SpEL expressions that can be used with @PreAuthorize.
     */
    public static class Compound {
        /**
         * SpEL expression to check for permissions needed to delete clubs.
         */
        public static final String DELETE_CLUBS = "hasRole('" + Code.MANAGE_CLUBS + "') and "
                + "hasRole('" + Code.MANAGE_PLAYERS + "')";


        /**
         * SpEL expression to check for permissions needed to delete leagues.
         */
        public static final String DELETE_LEAGUES = "hasRole('" + Code.MANAGE_LEAGUES + "') and "
                + "hasRole('" + Code.MANAGE_CLUBS + "') and "
                + "hasRole('" + Code.MANAGE_PLAYERS + "')";
    }

}
