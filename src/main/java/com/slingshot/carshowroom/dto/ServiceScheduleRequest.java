package com.slingshot.carshowroom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record ServiceScheduleRequest(
        @NotBlank @Pattern(regexp = "[A-HJ-NPR-Z0-9]{17}", message = "VIN must be 17 alphanumeric characters") String vin,
        @NotBlank String serviceType,
        @NotNull LocalDate date,
        String comments,
        Integer staffId
) {}
