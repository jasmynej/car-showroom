package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.ScheduleStatus;
import com.slingshot.carshowroom.model.ServiceSchedule;

import java.time.LocalDate;

public record ServiceScheduleResponse(
        Integer serviceId,
        String vin,
        String serviceType,
        LocalDate date,
        ScheduleStatus status,
        String comments,
        Integer staffId
) {
    public static ServiceScheduleResponse from(ServiceSchedule ss) {
        return new ServiceScheduleResponse(
                ss.getServiceId(), ss.getVin(), ss.getServiceType(),
                ss.getDate(), ss.getStatus(), ss.getComments(), ss.getStaffId()
        );
    }
}
