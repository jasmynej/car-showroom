package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for registering a new user")
public record UserRequest(
        @Schema(example = "Emma Davis") @NotBlank String name,
        @Schema(example = "emma@example.com") @NotBlank @Email String email,
        @Schema(example = "secret123") @NotBlank String password,
        @Schema(example = "555-0202") String contactInfo,
        @Schema(description = "CUSTOMER | STAFF | MANAGER") @NotNull Role role,
        @Schema(description = "Required for MANAGER role", example = "Sales") String department,
        @Schema(description = "Required for STAFF role", example = "Sales Representative") String designation
) {}
