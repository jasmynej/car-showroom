package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.CreateUserRequest;
import com.slingshot.carshowroom.dto.LoginRequest;
import com.slingshot.carshowroom.dto.LoginResponse;
import com.slingshot.carshowroom.dto.UserResponse;
import com.slingshot.carshowroom.service.AuthService;
import com.slingshot.carshowroom.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "User authentication endpoints")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @Operation(summary = "User login", description = "Authenticate user with userId, password, and role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials or role mismatch"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/login")
    public ResponseEntity&lt;LoginResponse&gt; login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "User signup", description = "Register a new user with userId, name, password, and optional role")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "409", description = "User ID already exists"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/signup")
    public ResponseEntity&lt;UserResponse&gt; signup(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUserFromAuth(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
