package com.shantanu.FinPilot.common.config;

import com.shantanu.FinPilot.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// CORS Imports
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // Custom JWT filter that validates JWT before every protected request
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Password Encoder Bean
     *
     * Used while:
     * - Registering a user (to hash password)
     * - Logging in (to compare passwords)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Main Spring Security Configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

                // Enable CORS using the configuration defined below
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Disable CSRF because we're building a REST API with JWT
                .csrf(csrf -> csrf.disable())

                // We don't use HTTP Sessions.
                // JWT makes the application stateless.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authorization Rules
                .authorizeHttpRequests(auth -> auth

                        // Authentication APIs are public
                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        // Every other API requires authentication
                        .anyRequest()
                        .authenticated()
                )

                // Execute JWT Filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Global CORS Configuration
     *
     * This allows the React application
     * running on localhost:5174
     * to communicate with Spring Boot.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // React Frontend URL
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173"
        ));

        // Allowed HTTP Methods
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"
        ));

        // Allow all request headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow Authorization header, Cookies etc.
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        // Apply CORS configuration to every endpoint
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}