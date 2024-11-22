package com.example.inventory_control_system.security;

import lombok.Getter;

/**
 * DTO for user authentication response.
 * Contains the JWT token and a message indicating authentication status.
 */
@Getter
public class AuthenticationResponse {

    private final String jwt;

    private final String message;

    public AuthenticationResponse(String jwt, String message) {
        this.jwt = jwt;
        this.message = message;
    }
}
