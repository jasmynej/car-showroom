package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String password,
        String contactInfo,
        @NotNull Role role,
        String department,
        String designation
) {}
