package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.LoginRequest;
import com.slingshot.carshowroom.dto.LoginResponse;
import com.slingshot.carshowroom.dto.UserRegistrationRequest;
import com.slingshot.carshowroom.dto.UserResponse;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for authentication endpoints.
 */
@Tag(name = "Authentication", description = "Login and registration endpoints")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * Register a new user.
     * @param request User registration data
     * @return User response with user details (no password)
     */
    @Operation(summary = "Register a new user", description = "Creates a new user with hashed password. Role defaults to CUSTOMER if not provided.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity&lt;?&gt; register(@Valid @RequestBody UserRegistrationRequest request) {
        User user = authService.registerUser(request);
        
        // Return user data without password
        Map&lt;String, Object&gt; response = new HashMap&lt;&gt;();
        response.put("userId", user.getUserId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Login endpoint.
     * @param request Login credentials (email, password, role)
     * @return Login response with user details if successful
     */
    @Operation(summary = "Login", description = "Authenticate user with email, password, and role. Returns user details on success.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials or role")
    })
    @PostMapping("/login")
    public ResponseEntity&lt;?&gt; login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.authenticateUser(request);
        
        if (response == null) {
            Map&lt;String, String&gt; error = new HashMap&lt;&gt;();
            error.put("error", "Invalid credentials or role");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        
        return ResponseEntity.ok(response);
    }
}
