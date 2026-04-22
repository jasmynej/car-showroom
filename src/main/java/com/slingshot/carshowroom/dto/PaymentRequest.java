package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record PaymentRequest(
        @NotNull Integer invoiceId,
        @NotNull Integer customerId,
        @NotBlank @Pattern(regexp = "[A-HJ-NPR-Z0-9]{17}", message = "VIN must be 17 alphanumeric characters") String vin,
        @NotNull @Positive Double amount,
        @NotNull PaymentMethod paymentMethod,
        // Cash fields
        String accountNumber,
        String pin,
        String bank,
        // Credit card fields
        String creditCardNumber,
        String cvvCode
) {}
