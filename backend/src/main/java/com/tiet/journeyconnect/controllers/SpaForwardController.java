package com.tiet.journeyconnect.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to forward known React SPA frontend client-side routes to index.html
 * when accessing the application directly via the Spring Boot embedded Tomcat on port 8080.
 */
@Controller
public class SpaForwardController {

    @GetMapping(value = {
        "/login",
        "/dashboard",
        "/communities",
        "/communities/**",
        "/travel-groups",
        "/travel-groups/**",
        "/my-journeys",
        "/profile",
        "/safety"
    })
    public String forwardClientRoutes() {
        return "forward:/index.html";
    }
}
