package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.ScheduleStatusRequest;
import com.slingshot.carshowroom.dto.ServiceScheduleRequest;
import com.slingshot.carshowroom.dto.ServiceScheduleResponse;
import com.slingshot.carshowroom.exception.ResourceNotFoundException;
import com.slingshot.carshowroom.model.ScheduleStatus;
import com.slingshot.carshowroom.model.ServiceSchedule;
import com.slingshot.carshowroom.repository.CarRepository;
import com.slingshot.carshowroom.repository.ServiceScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceScheduleService {

    private final ServiceScheduleRepository serviceScheduleRepository;
    private final CarRepository carRepository;

    public ServiceScheduleService(ServiceScheduleRepository serviceScheduleRepository, CarRepository carRepository) {
        this.serviceScheduleRepository = serviceScheduleRepository;
        this.carRepository = carRepository;
    }

    public List<ServiceScheduleResponse> getAllServices() {
        return serviceScheduleRepository.findAll().stream().map(ServiceScheduleResponse::from).toList();
    }

    public ServiceScheduleResponse getService(Integer id) {
        return ServiceScheduleResponse.from(findOrThrow(id));
    }

    public ServiceScheduleResponse scheduleService(ServiceScheduleRequest request) {
        if (!carRepository.existsById(request.vin())) {
            throw new ResourceNotFoundException("Car not found: " + request.vin());
        }
        ServiceSchedule ss = new ServiceSchedule();
        ss.setVin(request.vin());
        ss.setServiceType(request.serviceType());
        ss.setDate(request.date());
        ss.setStatus(ScheduleStatus.SCHEDULED);
        ss.setComments(request.comments());
        ss.setStaffId(request.staffId());
        return ServiceScheduleResponse.from(serviceScheduleRepository.save(ss));
    }

    public ServiceScheduleResponse updateStatus(Integer id, ScheduleStatusRequest request) {
        ServiceSchedule ss = findOrThrow(id);
        ss.setStatus(request.status());
        return ServiceScheduleResponse.from(serviceScheduleRepository.save(ss));
    }

    private ServiceSchedule findOrThrow(Integer id) {
        return serviceScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service schedule not found: " + id));
    }
}
