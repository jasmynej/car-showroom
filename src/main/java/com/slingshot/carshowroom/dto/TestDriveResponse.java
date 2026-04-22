package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.ScheduleStatus;
import com.slingshot.carshowroom.model.TestDrive;

import java.time.LocalDate;

public record TestDriveResponse(
        Integer testDriveId,
        String vin,
        Integer customerId,
        LocalDate date,
        String time,
        ScheduleStatus status,
        String comments
) {
    public static TestDriveResponse from(TestDrive td) {
        return new TestDriveResponse(
                td.getTestDriveId(), td.getVin(), td.getCustomerId(),
                td.getDate(), td.getTime(), td.getStatus(), td.getComments()
        );
    }
}
