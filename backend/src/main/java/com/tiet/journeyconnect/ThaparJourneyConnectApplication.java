package com.tiet.journeyconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class for Thapar Journey Connect — Java Lab Edition.
 * Bootstraps the Spring Boot 3.3.4 container on port 8080 with embedded H2 database.
 */
@SpringBootApplication
public class ThaparJourneyConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThaparJourneyConnectApplication.class, args);
    }
}
