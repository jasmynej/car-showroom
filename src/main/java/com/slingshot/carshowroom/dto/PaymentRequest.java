package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request body for making a payment. Provide cash fields for CASH, card fields for CREDIT_CARD.")
public record PaymentRequest(
        @Schema(description = "ID of the invoice to pay", example = "2") @NotNull Integer invoiceId,
        @Schema(description = "ID of the paying customer", example = "5") @NotNull Integer customerId,
        @Schema(description = "VIN of the car being purchased", example = "3VWFE21C04M000001")
        @NotBlank @Pattern(regexp = "[A-HJ-NPR-Z0-9]{17}", message = "VIN must be 17 alphanumeric characters") String vin,
        @Schema(example = "24860.00") @NotNull @Positive Double amount,
        @Schema(description = "CASH or CREDIT_CARD") @NotNull PaymentMethod paymentMethod,

        @Schema(description = "CASH only", example = "123456789") String accountNumber,
        @Schema(description = "CASH only", example = "4321") String pin,
        @Schema(description = "CASH only", example = "TD Bank") String bank,

        @Schema(description = "CREDIT_CARD only", example = "4111111111111111") String creditCardNumber,
        @Schema(description = "CREDIT_CARD only", example = "123") String cvvCode
) {}
