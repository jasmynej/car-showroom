package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.AvailabilityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record CarRequest(
        @NotBlank @Pattern(regexp = "[A-HJ-NPR-Z0-9]{17}", message = "VIN must be 17 alphanumeric characters") String vin,
        @NotBlank String make,
        @NotBlank String model,
        @NotNull Integer year,
        @NotNull Double price,
        String color,
        Double mileage,
        AvailabilityStatus availabilityStatus,
        LocalDate lastServiceDate
) {}
