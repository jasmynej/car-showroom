package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.CarRequest;
import com.slingshot.carshowroom.dto.CarResponse;
import com.slingshot.carshowroom.exception.ConflictException;
import com.slingshot.carshowroom.exception.ResourceNotFoundException;
import com.slingshot.carshowroom.model.AvailabilityStatus;
import com.slingshot.carshowroom.model.Car;
import com.slingshot.carshowroom.model.OrderStatus;
import com.slingshot.carshowroom.repository.CarRepository;
import com.slingshot.carshowroom.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    public CarService(CarRepository carRepository, PurchaseOrderRepository purchaseOrderRepository) {
        this.carRepository = carRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    public List<CarResponse> getAllCars() {
        return carRepository.findAll().stream().map(CarResponse::from).toList();
    }

    public List<CarResponse> getAvailableCars() {
        return carRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE)
                .stream().map(CarResponse::from).toList();
    }

    public CarResponse getCar(String vin) {
        return CarResponse.from(findOrThrow(vin));
    }

    public CarResponse addCar(CarRequest request) {
        if (carRepository.existsById(request.vin())) {
            throw new ConflictException("Car with VIN " + request.vin() + " already exists");
        }
        Car car = new Car();
        car.setVin(request.vin());
        car.setMake(request.make());
        car.setModel(request.model());
        car.setYear(request.year());
        car.setPrice(request.price());
        car.setColor(request.color());
        car.setMileage(request.mileage());
        car.setAvailabilityStatus(
                request.availabilityStatus() != null ? request.availabilityStatus() : AvailabilityStatus.AVAILABLE);
        car.setLastServiceDate(request.lastServiceDate());
        car.setLastUpdated(LocalDate.now());
        return CarResponse.from(carRepository.save(car));
    }

    public CarResponse updateCar(String vin, CarRequest request) {
        Car car = findOrThrow(vin);
        if (request.make() != null) car.setMake(request.make());
        if (request.model() != null) car.setModel(request.model());
        if (request.year() != null) car.setYear(request.year());
        if (request.price() != null) car.setPrice(request.price());
        if (request.color() != null) car.setColor(request.color());
        if (request.mileage() != null) car.setMileage(request.mileage());
        if (request.availabilityStatus() != null) car.setAvailabilityStatus(request.availabilityStatus());
        if (request.lastServiceDate() != null) car.setLastServiceDate(request.lastServiceDate());
        car.setLastUpdated(LocalDate.now());
        return CarResponse.from(carRepository.save(car));
    }

    public void deleteCar(String vin) {
        findOrThrow(vin);
        if (purchaseOrderRepository.existsByVinAndStatusIn(vin,
                List.of(OrderStatus.PENDING, OrderStatus.APPROVED))) {
            throw new ConflictException("Cannot delete car with active purchase orders");
        }
        carRepository.deleteById(vin);
    }

    private Car findOrThrow(String vin) {
        return carRepository.findById(vin)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + vin));
    }
}
