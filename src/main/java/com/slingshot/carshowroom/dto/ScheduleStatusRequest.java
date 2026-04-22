package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.ScheduleStatus;
import jakarta.validation.constraints.NotNull;

public record ScheduleStatusRequest(@NotNull ScheduleStatus status) {}
