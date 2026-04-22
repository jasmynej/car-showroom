package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.ScheduleStatusRequest;
import com.slingshot.carshowroom.dto.TestDriveRequest;
import com.slingshot.carshowroom.dto.TestDriveResponse;
import com.slingshot.carshowroom.exception.ResourceNotFoundException;
import com.slingshot.carshowroom.model.ScheduleStatus;
import com.slingshot.carshowroom.model.TestDrive;
import com.slingshot.carshowroom.repository.CarRepository;
import com.slingshot.carshowroom.repository.TestDriveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestDriveService {

    private final TestDriveRepository testDriveRepository;
    private final CarRepository carRepository;

    public TestDriveService(TestDriveRepository testDriveRepository, CarRepository carRepository) {
        this.testDriveRepository = testDriveRepository;
        this.carRepository = carRepository;
    }

    public List<TestDriveResponse> getAllTestDrives() {
        return testDriveRepository.findAll().stream().map(TestDriveResponse::from).toList();
    }

    public TestDriveResponse getTestDrive(Integer id) {
        return TestDriveResponse.from(findOrThrow(id));
    }

    public TestDriveResponse scheduleTestDrive(TestDriveRequest request) {
        if (!carRepository.existsById(request.vin())) {
            throw new ResourceNotFoundException("Car not found: " + request.vin());
        }
        TestDrive td = new TestDrive();
        td.setVin(request.vin());
        td.setCustomerId(request.customerId());
        td.setDate(request.date());
        td.setTime(request.time());
        td.setStatus(ScheduleStatus.SCHEDULED);
        td.setComments(request.comments());
        return TestDriveResponse.from(testDriveRepository.save(td));
    }

    public TestDriveResponse updateStatus(Integer id, ScheduleStatusRequest request) {
        TestDrive td = findOrThrow(id);
        td.setStatus(request.status());
        return TestDriveResponse.from(testDriveRepository.save(td));
    }

    private TestDrive findOrThrow(Integer id) {
        return testDriveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test drive not found: " + id));
    }
}
