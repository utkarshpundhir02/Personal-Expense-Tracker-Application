package com.expensetracker.infrastructure.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final String JWT_SECRET = "ifdvmdkcjsmklmxncklbnbvgcnvbhvgcfcgvbnbcgcgvvbccvvbgvjhvhjvhmcgfhjvbhbhjvhbjhvhfcghvmbvhbhjbhj";
    private final long JWT_EXPIRATION = 604800000L;

    public String generateToken(String username) {
        logger.info("Generating token for username: {}", username);
        
        try {
            String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();

            logger.info("Token successfully generated for username: {}", username);
            return token;
        } catch (Exception e) {
            logger.error("Error generating token for username: {}", username, e);
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public String getUsernameFromToken(String token) {
        logger.info("Extracting username from token...");

        try {
            String username = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

            logger.info("Username extracted from token: {}", username);
            return username;
        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", token, e);
            throw new IllegalArgumentException("Invalid token", e);
        }
    }
    
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(JWT_SECRET).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        logger.info("Validating token...");

        try {
            Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token);

            logger.info("Token is valid.");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid or expired JWT: {}", token, e);
            return false;
        }
    }
}
