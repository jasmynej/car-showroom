package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.LoginRequest;
import com.slingshot.carshowroom.dto.LoginResponse;
import com.slingshot.carshowroom.dto.PasswordUpdateRequest;
import com.slingshot.carshowroom.dto.UserRegistrationRequest;
import com.slingshot.carshowroom.exception.ConflictException;
import com.slingshot.carshowroom.exception.ResourceNotFoundException;
import com.slingshot.carshowroom.model.Role;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for handling authentication and user management.
 */
@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Register a new user with BCrypt password hashing.
     * 
     * @param request User registration details
     * @return Created user
     * @throws ConflictException if email already exists
     */
    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        
        // Hash password with BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);
        
        // Set role, default to CUSTOMER if not provided
        user.setRole(request.getRole() != null ? request.getRole() : Role.CUSTOMER);
        user.setContactInfo(request.getContactInfo());
        user.setDepartment(request.getDepartment());
        user.setDesignation(request.getDesignation());
        
        return userRepository.save(user);
    }
    
    /**
     * Authenticate user and verify role.
     * 
     * @param request Login credentials
     * @return Login response with user details
     * @throws IllegalArgumentException if credentials are invalid
     */
    public LoginResponse login(LoginRequest request) {
        // Find user by email
        Optional&lt;User&gt; userOpt = userRepository.findByEmail(request.getEmail());
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        User user = userOpt.get();
        
        // Verify password using BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        // Verify role matches
        if (!user.getRole().equals(request.getRole())) {
            throw new IllegalArgumentException("Invalid role for this user");
        }
        
        return new LoginResponse(user.getUserId(), user.getEmail(), user.getName(), user.getRole());
    }
    
    /**
     * Update user password.
     * 
     * @param userId User ID
     * @param request Password update request
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public void updatePassword(Integer userId, PasswordUpdateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -&gt; new ResourceNotFoundException("User not found"));
        
        // Hash new password with BCrypt
        String hashedPassword = passwordEncoder.encode(request.password());
        user.setPassword(hashedPassword);
        
        userRepository.save(user);
    }
}
