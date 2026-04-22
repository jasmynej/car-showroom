package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.ScheduleStatusRequest;
import com.slingshot.carshowroom.dto.TestDriveRequest;
import com.slingshot.carshowroom.dto.TestDriveResponse;
import com.slingshot.carshowroom.service.TestDriveService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-drives")
public class TestDriveController {

    private final TestDriveService testDriveService;

    public TestDriveController(TestDriveService testDriveService) {
        this.testDriveService = testDriveService;
    }

    @GetMapping
    public List<TestDriveResponse> getAllTestDrives() {
        return testDriveService.getAllTestDrives();
    }

    @GetMapping("/{id}")
    public TestDriveResponse getTestDrive(@PathVariable Integer id) {
        return testDriveService.getTestDrive(id);
    }

    @PostMapping
    public ResponseEntity<TestDriveResponse> scheduleTestDrive(@Valid @RequestBody TestDriveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(testDriveService.scheduleTestDrive(request));
    }

    @PatchMapping("/{id}/status")
    public TestDriveResponse updateStatus(@PathVariable Integer id,
                                          @Valid @RequestBody ScheduleStatusRequest request) {
        return testDriveService.updateStatus(id, request);
    }
}
