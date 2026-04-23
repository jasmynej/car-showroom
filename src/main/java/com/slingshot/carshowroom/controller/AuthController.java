package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.LoginRequest;
import com.slingshot.carshowroom.dto.LoginResponse;
import com.slingshot.carshowroom.dto.PasswordUpdateRequest;
import com.slingshot.carshowroom.dto.UserRegistrationRequest;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for authentication and user management endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Authentication", description = "Authentication and user management APIs")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * Register a new user.
     * 
     * @param request User registration details
     * @return Created user details (without password)
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with BCrypt password hashing")
    public ResponseEntity&lt;?&gt; registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            User user = authService.registerUser(request);
            // Don't return password in response
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "userId", user.getUserId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "role", user.getRole()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Authenticate user.
     * 
     * @param request Login credentials
     * @return User details if authentication successful
     */
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user with email, password, and role")
    public ResponseEntity&lt;?&gt; login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update user password.
     * 
     * @param userId User ID
     * @param request New password
     * @return Success message
     */
    @PutMapping("/users/{userId}/password")
    @Operation(summary = "Update password", description = "Updates user password with BCrypt hashing")
    public ResponseEntity&lt;?&gt; updatePassword(
            @PathVariable Integer userId,
            @Valid @RequestBody PasswordUpdateRequest request) {
        try {
            authService.updatePassword(userId, request);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
