package com.slingshot.carshowroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request body for generating an invoice. totalAmount is computed server-side as price + (price × tax / 100).")
public record InvoiceRequest(
        @Schema(description = "ID of the purchase order (must be APPROVED)", example = "2") @NotNull Integer orderId,
        @Schema(description = "ID of the customer", example = "5") @NotNull Integer customerId,
        @Schema(description = "Car price before tax", example = "22000.00") @NotNull @Positive Double price,
        @Schema(description = "Tax percentage (e.g. 13 for 13%)", example = "13.00") @NotNull @Positive Double tax,
        @Schema(example = "Payment due within 7 days.") String termsAndConditions
) {}
