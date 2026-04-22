package com.slingshot.carshowroom.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordUpdateRequest(@NotBlank String password) {}
