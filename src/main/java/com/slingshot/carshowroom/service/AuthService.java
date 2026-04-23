package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.LoginRequest;
import com.slingshot.carshowroom.dto.LoginResponse;
import com.slingshot.carshowroom.dto.UserRegistrationRequest;
import com.slingshot.carshowroom.exception.ConflictException;
import com.slingshot.carshowroom.model.Role;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for authentication and user registration.
 */
@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Register a new user with hashed password.
     * @param request User registration data
     * @return Registered user
     * @throws ConflictException if email already exists
     */
    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setContactInfo(request.getContactInfo());
        
        // Default to CUSTOMER if role not provided
        user.setRole(request.getRole() != null ? request.getRole() : Role.CUSTOMER);
        
        user.setDepartment(request.getDepartment());
        user.setDesignation(request.getDesignation());
        
        return userRepository.save(user);
    }
    
    /**
     * Authenticate user with email, password, and role.
     * @param request Login credentials
     * @return LoginResponse if successful, null otherwise
     */
    public LoginResponse authenticateUser(LoginRequest request) {
        Optional&lt;User&gt; userOpt = userRepository.findByEmail(request.getEmail());
        
        if (userOpt.isEmpty()) {
            return null; // User not found
        }
        
        User user = userOpt.get();
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return null; // Invalid password
        }
        
        // Verify role matches
        if (!user.getRole().equals(request.getRole())) {
            return null; // Wrong role
        }
        
        // Authentication successful
        return new LoginResponse(
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }
}
