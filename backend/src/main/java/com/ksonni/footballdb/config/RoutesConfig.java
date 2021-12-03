package com.ksonni.footballdb.config;

public class RoutesConfig {

    private static final String API_BASE_PATH = "/api/v1/";

    public static class Leagues {
        public static final String PATH = API_BASE_PATH + "leagues";
    }

    public static class Clubs {
        public static final String PATH = API_BASE_PATH + "clubs";
    }

    public static class Players {
        public static final String PATH = API_BASE_PATH + "players";
    }

    public static class Auth {
        public static final String PATH = API_BASE_PATH + "auth";

        public static final String REGISTER = "/register";
        public static final String REGISTER_PATH = PATH + REGISTER;
    }

}
