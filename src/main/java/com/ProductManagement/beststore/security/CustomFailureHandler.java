package com.ProductManagement.beststore.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomFailureHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        // Set error message attribute to be displayed on the error page
        request.setAttribute("errorMessage", "You do not have permission to access this page!");

        // Get RequestDispatcher to forward the request to the error page
        RequestDispatcher dispatcher = request.getRequestDispatcher("/access-denied");
        dispatcher.forward(request, response);
    }
}

