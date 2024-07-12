package com.ProductManagement.beststore.controller;

public class AuthenticationResponse {

    private String token; // Private field to hold the authentication token

    // Constructor to initialize the AuthResponse with a token
    public AuthenticationResponse(String token) {
        this.token = token;
    }

    // Getter for token field
    public String getToken() {
        return token;
    }

    // Setter for token field
    public void setToken(String token) {
        this.token = token;
    }
}

