package com.example.todoapp.controller;

import com.example.todoapp.dto.RegisterRequest;
import com.example.todoapp.model.User;
import com.example.todoapp.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        // Simple check to avoid duplicate usernames
        if (userRepository.findByUsername(request.getUsername()) != null) {
            logger.warn("Registration attempt with existing username: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()) != null) {
            logger.warn("Registration attempt with existing email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Email already exists");
        }

        // Hash the password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(request.getPassword());
        User user = new User(request.getUsername(), request.getEmail(), hashedPassword);
        logger.info("Registering new user: {}", user);
        return ResponseEntity.ok(userRepository.save(user));
    }

    // Mock "login" by finding user by username
    @GetMapping("/login")
    public User login(@RequestParam String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("User not found");
        logger.info("User logged in: {}", user);
        return user;
    }
}
