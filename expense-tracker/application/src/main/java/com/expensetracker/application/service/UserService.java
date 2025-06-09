package com.expensetracker.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expensetracker.domain.model.User;
import com.expensetracker.domain.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
	@Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String name, String email, String passwordHash) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User(name, email, passwordHash);
        return userRepository.save(user);
    }
    
    public Optional<User> findByEmail(String email){
    	return userRepository.findByEmail(email);
    }
}
