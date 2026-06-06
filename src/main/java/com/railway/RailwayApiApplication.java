package com.railway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class RailwayApiApplication {

    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println(" RAILWAY BOOKING SYSTEM - REST API");
        System.out.println("===============================================");
        System.out.println();
        System.out.println("Starting Railway Booking API Server...");
        System.out.println();

        SpringApplication.run(RailwayApiApplication.class, args);

        System.out.println();
        System.out.println("===============================================");
        System.out.println(" API SERVER STARTED SUCCESSFULLY!");
        System.out.println("===============================================");
        System.out.println();
        System.out.println("API Endpoints available at: http://localhost:8080");
        System.out.println();
        System.out.println("Available endpoints:");
        System.out.println("  - GET    /api/trains                  - Get all trains");
        System.out.println("  - GET    /api/trains/search           - Search trains");
        System.out.println("  - POST   /api/trains                  - Add new train");
        System.out.println("  - GET    /api/bookings                - Get all bookings");
        System.out.println("  - POST   /api/bookings                - Book a ticket");
        System.out.println("  - DELETE /api/bookings/{pnr}          - Cancel booking");
        System.out.println("  - POST   /api/users/register          - Register user");
        System.out.println("  - POST   /api/users/login             - User login");
        System.out.println("  - GET    /api/users/{username}        - Get user profile");
        System.out.println("  - POST   /api/feedback                - Submit feedback");
        System.out.println("  - GET    /api/feedback                - Get all feedback");
        System.out.println();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}