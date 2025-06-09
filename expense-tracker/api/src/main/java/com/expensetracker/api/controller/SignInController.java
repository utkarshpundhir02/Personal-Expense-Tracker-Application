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

import com.expensetracker.api.dto.UserResponseDTO;
import com.expensetracker.application.service.UserService;
import com.expensetracker.domain.model.User;

@RestController
@RequestMapping("/users")
public class SignInController {

    private final UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public SignInController(UserService userService) {
        this.userService = userService;
    }

    // DTO for user registration request
    public static class RegisterRequest {
        public String name;
        public String email;
        public String password;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
        	Optional<User> existingUser = userService.findByEmail(request.email);
        	
        	if(!existingUser.isEmpty()) {
        		return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        	}
            String passwordHash = passwordEncoder.encode(request.password);
            User user = userService.registerUser(request.name, request.email, passwordHash);
            UserResponseDTO response = new UserResponseDTO(user.getName(), user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
