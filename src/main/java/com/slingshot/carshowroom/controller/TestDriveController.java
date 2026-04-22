package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.ScheduleStatusRequest;
import com.slingshot.carshowroom.dto.TestDriveRequest;
import com.slingshot.carshowroom.dto.TestDriveResponse;
import com.slingshot.carshowroom.service.TestDriveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Test Drives", description = "Test drive scheduling")
@RestController
@RequestMapping("/api/test-drives")
public class TestDriveController {

    private final TestDriveService testDriveService;

    public TestDriveController(TestDriveService testDriveService) {
        this.testDriveService = testDriveService;
    }

    @Operation(summary = "List all test drives")
    @GetMapping
    public List<TestDriveResponse> getAllTestDrives() {
        return testDriveService.getAllTestDrives();
    }

    @Operation(summary = "Get a test drive by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test drive found"),
            @ApiResponse(responseCode = "404", description = "Test drive not found")
    })
    @GetMapping("/{id}")
    public TestDriveResponse getTestDrive(@PathVariable Integer id) {
        return testDriveService.getTestDrive(id);
    }

    @Operation(summary = "Schedule a test drive", description = "Car must exist. Status is set to `SCHEDULED`.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Test drive scheduled"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @PostMapping
    public ResponseEntity<TestDriveResponse> scheduleTestDrive(@Valid @RequestBody TestDriveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(testDriveService.scheduleTestDrive(request));
    }

    @Operation(summary = "Update test drive status", description = "Allowed values: `COMPLETED`, `CANCELLED`.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Test drive not found")
    })
    @PatchMapping("/{id}/status")
    public TestDriveResponse updateStatus(@Parameter(description = "Test drive ID") @PathVariable Integer id,
                                          @Valid @RequestBody ScheduleStatusRequest request) {
        return testDriveService.updateStatus(id, request);
    }
}
