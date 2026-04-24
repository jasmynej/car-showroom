package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.LoginRequest;
import com.slingshot.carshowroom.dto.UserResponse;
import com.slingshot.carshowroom.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Login and authentication endpoints")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Login with credentials", description = "Validates credentials and role, returns user data on success")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials or wrong role"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/login")
    public ResponseEntity&lt;UserResponse&gt; login(@Valid @RequestBody LoginRequest request) {
        UserResponse response = userService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}
