package com.ksonni.footballdb.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

public class RoutesConfig {

    @Getter
    @AllArgsConstructor
    static class UnauthenticatedRoute {
        private final HttpMethod method;
        private final String pattern;
    }

    private static final String API_BASE_PATH = "/api/v1";

    public static class Leagues {
        public static final String PATH = API_BASE_PATH + "/leagues";
    }

    public static class Clubs {
        public static final String PATH = API_BASE_PATH + "/clubs";
    }

    public static class Players {
        public static final String PATH = API_BASE_PATH + "/players";
    }

    public static class Auth {
        public static final String PATH = API_BASE_PATH + "/auth";

        public static final String REGISTER = "/register";
        public static final String REGISTER_PATH = PATH + REGISTER;

        public static final String LOGIN = "/login";
        public static final String LOGIN_PATH = PATH + LOGIN;

        public static final String LOGOUT = "/logout";
        public static final String LOGOUT_PATH = PATH + LOGOUT;

        public static final String ME = "/me";
        public static final String ME_PATH = PATH + ME;
    }

    public static class Users {
        public static final String PATH = API_BASE_PATH + "/users";
    }

    public static final List<UnauthenticatedRoute> UNAUTHENTICATED_ROUTES = Arrays.asList(
        // Auth
        new UnauthenticatedRoute(HttpMethod.POST, Auth.LOGIN_PATH),
        new UnauthenticatedRoute(HttpMethod.POST, Auth.REGISTER_PATH),
        new UnauthenticatedRoute(HttpMethod.POST, Auth.LOGOUT_PATH),

        // Queries
        new UnauthenticatedRoute(HttpMethod.GET, Leagues.PATH),
        new UnauthenticatedRoute(HttpMethod.GET, Clubs.PATH),
        new UnauthenticatedRoute(HttpMethod.GET, Players.PATH)
    );

}
