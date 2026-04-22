package com.slingshot.carshowroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Schema(description = "Request body for scheduling a vehicle service")
public record ServiceScheduleRequest(
        @Schema(description = "VIN of the car", example = "2T1BURHE0JC043821")
        @NotBlank @Pattern(regexp = "[A-HJ-NPR-Z0-9]{17}", message = "VIN must be 17 alphanumeric characters") String vin,
        @Schema(example = "Tire Rotation") @NotBlank String serviceType,
        @Schema(example = "2025-02-10") @NotNull LocalDate date,
        @Schema(example = "Scheduled for regular tire rotation.") String comments,
        @Schema(description = "Optional staff member assigned to the service", example = "3") Integer staffId
) {}
