package com.ProductManagement.beststore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

import com.ProductManagement.beststore.models.MyUser;
import com.ProductManagement.beststore.models.Product;
import com.ProductManagement.beststore.repository.ProductRepository;
import com.ProductManagement.beststore.repository.UserRepository;
import com.ProductManagement.beststore.security.JwtUtil;


@Service

public class AuthenticationService {

    @Autowired
    private PasswordEncoder passwordEncoder; // Autowires the PasswordEncoder dependency

    @Autowired
    private AuthenticationManager authenticationManager; // Autowires the AuthenticationManager dependency

    @Autowired
    private UserDetailsService userDetailsService; // Autowires the UserDetailsService dependency

    @Autowired
    private JwtUtil jwtUtil; // Autowires the JwtUtil dependency
    
    @Autowired
    private UserRepository userRepository;

    // Handles user login authentication
    public AuthenticationResponse login(String username, String password) {
        // Authenticate user credentials using AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // Load user details based on username
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Generate JWT token for authenticated user
        String token = jwtUtil.generateToken(userDetails);

        // Return AuthResponse containing JWT token
        return new AuthenticationResponse(token);
    }

    // Handles user registration
    public void register(RegisterRequest registerRequest) {
        
        // Save the user entity to the database using UserDetailsManager
        ((JdbcUserDetailsManager) userDetailsService).createUser(User.withUsername(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(registerRequest.getRole())
                .build());
        
        MyUser newUser = new MyUser();
        newUser.setName(registerRequest.getUsername()); // Assuming username is stored in name field
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setRole(registerRequest.getRole());

        // Save the user using UserRepository
        userRepository.save(newUser);
       
    }    
    
}
