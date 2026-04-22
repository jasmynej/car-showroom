package com.slingshot.carshowroom.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "test_drive")
public class TestDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer testDriveId;

    @Column(nullable = false, length = 17)
    private String vin;

    @Column(nullable = false)
    private Integer customerId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus status;

    private String comments;

    public TestDrive() {}

    public Integer getTestDriveId() { return testDriveId; }
    public void setTestDriveId(Integer testDriveId) { this.testDriveId = testDriveId; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public ScheduleStatus getStatus() { return status; }
    public void setStatus(ScheduleStatus status) { this.status = status; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
