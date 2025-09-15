package com.enterprise.airbnb.graphql.dto;

import com.enterprise.airbnb.model.User;

/**
 * GraphQL Response DTO for Authentication
 */
public class AuthResponse {
    
    private User user;
    private String token;
    private String message;

    // Constructors
    public AuthResponse() {}

    // Getters and Setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}



