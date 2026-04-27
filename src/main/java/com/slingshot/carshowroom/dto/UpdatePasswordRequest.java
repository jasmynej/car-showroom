package com.slingshot.carshowroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for updating password")
public record UpdatePasswordRequest(
        @Schema(example = "NewSecurePass456!") 
        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String newPassword
) {}
