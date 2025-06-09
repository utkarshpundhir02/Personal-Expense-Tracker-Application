package com.expensetracker.infrastructure.security;

import com.expensetracker.infrastructure.security.filters.JwtAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        logger.info("SecurityConfig instantiated and JwtAuthenticationFilter injected.");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring SecurityFilterChain...");

        return http
                // Disable CSRF globally for JWT setup
                .csrf(csrf -> {
                    logger.info("Disabling CSRF protection for JWT setup...");
                    csrf
                        .ignoringRequestMatchers("/h2-console/**")
                        .disable();
                })
                // Stateless session management (as we are using JWT)
                .sessionManagement(sess -> {
                    logger.info("Setting session management to stateless (for JWT)...");                    
                    sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                // Authorize requests
                .authorizeHttpRequests(auth -> {
                    logger.info("Configuring request authorization...");
                    auth
                        .requestMatchers("/auth/**", "/users/register", "/users/login", "/h2-console/**").permitAll()
                        .requestMatchers("/expenses/**").permitAll()
                        .anyRequest().authenticated();
                })
                // Allow H2 console frames (iframe) from same origin
                .headers(headers -> {
                    logger.info("Allowing H2 console frames...");
                    headers.frameOptions(frame -> frame.sameOrigin());
                })
                // Add the JWT filter before the UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Creating BCryptPasswordEncoder bean...");
        return new BCryptPasswordEncoder();
    }
}
