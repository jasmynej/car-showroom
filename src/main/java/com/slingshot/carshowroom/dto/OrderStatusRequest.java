package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusRequest(@NotNull OrderStatus status) {}
