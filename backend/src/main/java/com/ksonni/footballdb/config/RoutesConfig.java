package com.ksonni.footballdb.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

public final class RoutesConfig {

    /**
     * Routes that do not need an authenticated session.
     */
    public static final List<UnauthenticatedRoute> UNAUTHENTICATED_ROUTES = Arrays.asList(
            // Auth
            new UnauthenticatedRoute(HttpMethod.POST, Auth.LOGIN_PATH),
            new UnauthenticatedRoute(HttpMethod.POST, Auth.REGISTER_PATH),
            new UnauthenticatedRoute(HttpMethod.POST, Auth.LOGOUT_PATH),

            // Queries
            new UnauthenticatedRoute(HttpMethod.GET, Leagues.PATH),
            new UnauthenticatedRoute(HttpMethod.GET, Clubs.PATH),
            new UnauthenticatedRoute(HttpMethod.GET, Players.PATH),

            // Docs
            new UnauthenticatedRoute(HttpMethod.GET, Docs.UI),
            new UnauthenticatedRoute(HttpMethod.GET, Docs.JSON).crossOrigin(true),
            new UnauthenticatedRoute(HttpMethod.GET, Docs.YML).crossOrigin(true)
    );

    private static final String API_BASE_PATH = "/api/v1";

    private RoutesConfig() {
    }

    @Getter
    @RequiredArgsConstructor
    static class UnauthenticatedRoute {
        private final HttpMethod method;
        private final String pattern;

        private boolean crossOrigin;

        UnauthenticatedRoute crossOrigin(final boolean crossOriginVal) {
            this.crossOrigin = crossOriginVal;
            return this;
        }
    }

    public static class Leagues {
        /**
         * Leagues base path.
         */
        public static final String PATH = API_BASE_PATH + "/leagues";
    }

    public static class Clubs {
        /**
         * Clubs base path.
         */
        public static final String PATH = API_BASE_PATH + "/clubs";
    }

    public static class Players {
        /**
         * Players base path.
         */
        public static final String PATH = API_BASE_PATH + "/players";
    }

    public static class Auth {
        /**
         * Auth base path.
         */
        public static final String PATH = API_BASE_PATH + "/auth";

        /**
         * User registration sub-path.
         */
        public static final String REGISTER = "/register";
        /**
         * User registration path.
         */
        public static final String REGISTER_PATH = PATH + REGISTER;

        /**
         * User login sub-path.
         */
        public static final String LOGIN = "/login";
        /**
         * User login path.
         */
        public static final String LOGIN_PATH = PATH + LOGIN;

        /**
         * Logout sub-path.
         */
        public static final String LOGOUT = "/logout";
        /**
         * Logout path.
         */
        public static final String LOGOUT_PATH = PATH + LOGOUT;

        /**
         * Me sub-path.
         */
        public static final String ME = "/me";
        /**
         * Me path.
         */
        public static final String ME_PATH = PATH + ME;
    }

    public static class Users {
        /**
         * Users base path.
         */
        public static final String PATH = API_BASE_PATH + "/users";
    }

    public static class Docs {
        /**
         * Endpoint that serves Swagger UI.
         */
        public static final String UI = "/swagger-ui/**";
        /**
         * Open API docs in JSON format.
         */
        public static final String JSON = "/v3/api-docs/**";
        /**
         * Open API docs in YAML format.
         */
        public static final String YML = "/v3/api-docs.yaml";
    }

}
