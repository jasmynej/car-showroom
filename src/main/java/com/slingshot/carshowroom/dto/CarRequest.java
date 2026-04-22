package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.AvailabilityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Schema(description = "Request body for creating or updating a car")
public record CarRequest(
        @Schema(description = "17-character alphanumeric VIN", example = "1HGCM82633A004352")
        @NotBlank @Pattern(regexp = "[A-HJ-NPR-Z0-9]{17}", message = "VIN must be 17 alphanumeric characters") String vin,

        @Schema(example = "Honda") @NotBlank String make,
        @Schema(example = "Accord") @NotBlank String model,
        @Schema(example = "2023") @NotNull Integer year,
        @Schema(example = "28500.00") @NotNull Double price,
        @Schema(example = "Silver") String color,
        @Schema(example = "1200.0") Double mileage,

        @Schema(description = "Defaults to AVAILABLE if omitted", example = "AVAILABLE")
        AvailabilityStatus availabilityStatus,

        @Schema(description = "Date of last service", example = "2024-12-01")
        LocalDate lastServiceDate
) {}
