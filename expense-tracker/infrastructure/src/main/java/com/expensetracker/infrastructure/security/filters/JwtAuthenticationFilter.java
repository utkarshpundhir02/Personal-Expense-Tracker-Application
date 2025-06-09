package com.expensetracker.infrastructure.security.filters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.expensetracker.infrastructure.security.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        logger.info("JwtAuthenticationFilter instantiated.");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        logger.info("JwtAuthenticationFilter - doFilterInternal() invoked.");

        // Get the token from the request (assuming it's in the Authorization header)
        String token = getTokenFromRequest(request);

        if (token != null) {
            logger.info("Token found: {}", token);

            if (jwtTokenProvider.validateToken(token)) {
                logger.info("Token is valid.");

                // Get authentication details from the token
                String username = jwtTokenProvider.getUsernameFromToken(token);
                logger.info("Username extracted from token: {}", username);

                // Create an authentication token
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, null);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in the context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authentication set in SecurityContextHolder.");
            } else {
                logger.warn("Invalid token.");
            }
        } else {
            logger.warn("No token found in the request.");
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
        logger.info("JwtAuthenticationFilter - filterChain.doFilter() completed.");
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            logger.info("Extracting token from Authorization header.");
            return bearerToken.substring(7); // Get the token after "Bearer "
        }
        return null;
    }
}
