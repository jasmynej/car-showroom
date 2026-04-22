package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.ScheduleStatusRequest;
import com.slingshot.carshowroom.dto.ServiceScheduleRequest;
import com.slingshot.carshowroom.dto.ServiceScheduleResponse;
import com.slingshot.carshowroom.service.ServiceScheduleService;
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

@Tag(name = "Service Schedules", description = "Vehicle service and maintenance")
@RestController
@RequestMapping("/api/services")
public class ServiceScheduleController {

    private final ServiceScheduleService serviceScheduleService;

    public ServiceScheduleController(ServiceScheduleService serviceScheduleService) {
        this.serviceScheduleService = serviceScheduleService;
    }

    @Operation(summary = "List all service schedules")
    @GetMapping
    public List<ServiceScheduleResponse> getAllServices() {
        return serviceScheduleService.getAllServices();
    }

    @Operation(summary = "Get a service schedule by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service schedule found"),
            @ApiResponse(responseCode = "404", description = "Service schedule not found")
    })
    @GetMapping("/{id}")
    public ServiceScheduleResponse getService(@PathVariable Integer id) {
        return serviceScheduleService.getService(id);
    }

    @Operation(summary = "Schedule a vehicle service", description = "Car must exist. Status is set to `SCHEDULED`.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Service scheduled"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @PostMapping
    public ResponseEntity<ServiceScheduleResponse> scheduleService(@Valid @RequestBody ServiceScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceScheduleService.scheduleService(request));
    }

    @Operation(summary = "Update service schedule status", description = "Allowed values: `COMPLETED`, `CANCELLED`.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Service schedule not found")
    })
    @PatchMapping("/{id}/status")
    public ServiceScheduleResponse updateStatus(@Parameter(description = "Service schedule ID") @PathVariable Integer id,
                                                @Valid @RequestBody ScheduleStatusRequest request) {
        return serviceScheduleService.updateStatus(id, request);
    }
}
