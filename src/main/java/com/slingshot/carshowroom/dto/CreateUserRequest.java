package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for user registration")
public record CreateUserRequest(
        @Schema(example = "john.doe") 
        @NotBlank(message = "User ID is required")
        @Size(min = 3, max = 50, message = "User ID must be between 3 and 50 characters")
        String userId,
        
        @Schema(example = "John Doe") 
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,
        
        @Schema(example = "SecurePass123!") 
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,
        
        @Schema(description = "CUSTOMER | STAFF | MANAGER. Defaults to CUSTOMER if not provided")
        String role
) {}
