package com.ksonni.footballdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FootballDbApplication {

    protected FootballDbApplication() {
    }

    /**
     * Spring boot main.
     *
     * @param args Program arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(FootballDbApplication.class, args);
    }

}
