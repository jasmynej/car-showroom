package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.ScheduleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for updating a test drive or service schedule status")
public record ScheduleStatusRequest(
        @Schema(description = "COMPLETED or CANCELLED") @NotNull ScheduleStatus status
) {}
