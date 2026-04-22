package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for updating a purchase order status")
public record OrderStatusRequest(
        @Schema(description = "APPROVED sets the car to RESERVED. COMPLETED is set automatically by payment.")
        @NotNull OrderStatus status
) {}
