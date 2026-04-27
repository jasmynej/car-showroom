package com.slingshot.carshowroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for user login")
public record LoginRequest(
        @Schema(example = "john.doe") 
        @NotBlank(message = "User ID is required")
        String userId,
        
        @Schema(example = "SecurePass123!") 
        @NotBlank(message = "Password is required")
        String password,
        
        @Schema(description = "CUSTOMER | STAFF | MANAGER") 
        @NotBlank(message = "Role is required")
        String role
) {}
