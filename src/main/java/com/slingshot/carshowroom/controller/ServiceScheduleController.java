package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.ScheduleStatusRequest;
import com.slingshot.carshowroom.dto.ServiceScheduleRequest;
import com.slingshot.carshowroom.dto.ServiceScheduleResponse;
import com.slingshot.carshowroom.service.ServiceScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceScheduleController {

    private final ServiceScheduleService serviceScheduleService;

    public ServiceScheduleController(ServiceScheduleService serviceScheduleService) {
        this.serviceScheduleService = serviceScheduleService;
    }

    @GetMapping
    public List<ServiceScheduleResponse> getAllServices() {
        return serviceScheduleService.getAllServices();
    }

    @GetMapping("/{id}")
    public ServiceScheduleResponse getService(@PathVariable Integer id) {
        return serviceScheduleService.getService(id);
    }

    @PostMapping
    public ResponseEntity<ServiceScheduleResponse> scheduleService(@Valid @RequestBody ServiceScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceScheduleService.scheduleService(request));
    }

    @PatchMapping("/{id}/status")
    public ServiceScheduleResponse updateStatus(@PathVariable Integer id,
                                                @Valid @RequestBody ScheduleStatusRequest request) {
        return serviceScheduleService.updateStatus(id, request);
    }
}
