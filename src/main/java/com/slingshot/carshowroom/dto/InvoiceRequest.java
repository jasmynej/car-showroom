package com.slingshot.carshowroom.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InvoiceRequest(
        @NotNull Integer orderId,
        @NotNull Integer customerId,
        @NotNull @Positive Double price,
        @NotNull @Positive Double tax,
        String termsAndConditions
) {}
