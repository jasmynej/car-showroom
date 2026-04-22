package com.slingshot.carshowroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Schema(description = "Request body for scheduling a test drive")
public record TestDriveRequest(
        @Schema(description = "VIN of the car to test drive", example = "5NPE24AF8FH052952")
        @NotBlank @Pattern(regexp = "[A-HJ-NPR-Z0-9]{17}", message = "VIN must be 17 alphanumeric characters") String vin,
        @Schema(description = "ID of the customer", example = "6") @NotNull Integer customerId,
        @Schema(example = "2025-02-01") @NotNull LocalDate date,
        @Schema(example = "10:00 AM") @NotBlank String time,
        @Schema(example = "Interested in the sports package.") String comments
) {}
