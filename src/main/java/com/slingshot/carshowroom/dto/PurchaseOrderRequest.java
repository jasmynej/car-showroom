package com.slingshot.carshowroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request body for creating a purchase order")
public record PurchaseOrderRequest(
        @Schema(description = "ID of the customer placing the order", example = "5")
        @NotNull Integer customerId,

        @Schema(description = "VIN of the car to purchase — must be AVAILABLE", example = "3VWFE21C04M000001")
        @NotBlank @Pattern(regexp = "[A-HJ-NPR-Z0-9]{17}", message = "VIN must be 17 alphanumeric characters") String vin,

        @Schema(example = "Interested in the blue Golf.") String comments
) {}
