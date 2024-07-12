package com.ProductManagement.beststore.security;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        // Default redirect URL
        String redirectURL = "/";

        // Check user's roles to determine the appropriate redirect URL
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                redirectURL = "/admin/jpa/products";
                break;
            } else if (authority.getAuthority().equals("ROLE_USER")) {
                redirectURL = "/user/jpa/products";
                break;
            }
        }

        // Generate JWT token for the authenticated user
        //generate a JWT token based on the authenticated principal (typically the username or user details).
        String token = jwtUtil.generateToken(authentication.getPrincipal());

        // Create a HTTP-only cookie to store the JWT token
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true); // Prevents JavaScript access to the cookie
        jwtCookie.setSecure(true); // Ensures cookie is only sent over HTTPS
        jwtCookie.setPath("/"); // Makes the cookie accessible to all paths of the application
        jwtCookie.setMaxAge(24 * 60 * 60); // Sets cookie expiration time (in seconds)

        // Add the JWT cookie to the response
        response.addCookie(jwtCookie);

        // Redirect the user to the determined URL based on their role
        response.sendRedirect(redirectURL);
    }

}





