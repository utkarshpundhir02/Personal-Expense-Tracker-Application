package com.expensetracker.api.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.application.service.UserService;
import com.expensetracker.domain.model.User;
import com.expensetracker.infrastructure.security.JwtTokenProvider;

@RestController
@RequestMapping("/users")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static class LoginRequest {
        public String email;
        public String password;
    }

    public static class LoginResponse {
        public String token;
        public LoginResponse(String token) {
            this.token = token;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userService.findByEmail(request.email);
        if (userOpt.isPresent() && passwordEncoder.matches(request.password, userOpt.get().getPasswordHash())) {
            String token = jwtUtil.generateToken(userOpt.get().getEmail());
            return ResponseEntity.ok(new LoginResponse(token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
