package com.ProductManagement.beststore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService; // Autowires the AuthService dependency

    // Handles POST requests to /auth/login endpoint
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestParam String username, @RequestParam String password) {
        // Delegates login operation to AuthService and returns ResponseEntity with AuthResponse
        return ResponseEntity.ok(authenticationService.login(username, password));
    }

    // Handles POST requests to /auth/register endpoint
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") RegisterRequest registerRequest) {
        // Delegates registration operation to AuthService
        authenticationService.register(registerRequest);
        // Returns ResponseEntity with success message upon successful registration
        return "/webDetails/HomePage";
    }
}
