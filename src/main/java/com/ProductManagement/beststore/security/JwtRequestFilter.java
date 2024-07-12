package com.ProductManagement.beststore.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    //examine the incoming requests for JWT in the header, look at the right header and see if JWT is valid
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
    	
    	//obtain header for authorization
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);	// Leaving the "Bearer " and accessing the JWT token directly in authorization header
//            System.out.println("---------------------"+jwt);
//            username = jwtUtil.extractUsername(jwt);
        }
        
        else
        {
        	Cookie[] cookies=request.getCookies();
        	if(cookies!=null)
        	{
        		for(Cookie cookie:cookies)
        		{
        			if("jwt".equals(cookie.getName()))
        			{
        				jwt=cookie.getValue();
        				break;
        			}
        		}
        	}
        	
        }
        
        if(jwt!=null)
        {
        	try {
        		username=jwtUtil.extractUsername(jwt);
        		
        		System.out.println("==================================================");
        		System.out.println(username);
        		System.out.println(jwt);
        		System.out.println("==================================================");

        	}
        	catch(Exception ex)
        	{
        		System.out.println("Exception: "+ex.getMessage());
        	}
        }
        
        //verifying that user is not inside the SecurityContext, simulate only when the SecurityContextHolder is null
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            //validate the username in JWT token with the username in userDetails
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                        );
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}





