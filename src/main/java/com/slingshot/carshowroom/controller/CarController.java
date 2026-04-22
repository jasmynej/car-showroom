package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.CarRequest;
import com.slingshot.carshowroom.dto.CarResponse;
import com.slingshot.carshowroom.service.CarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public List<CarResponse> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/available")
    public List<CarResponse> getAvailableCars() {
        return carService.getAvailableCars();
    }

    @GetMapping("/{vin}")
    public CarResponse getCar(@PathVariable String vin) {
        return carService.getCar(vin);
    }

    @PostMapping
    public ResponseEntity<CarResponse> addCar(@Valid @RequestBody CarRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.addCar(request));
    }

    @PutMapping("/{vin}")
    public CarResponse updateCar(@PathVariable String vin, @RequestBody CarRequest request) {
        return carService.updateCar(vin, request);
    }

    @DeleteMapping("/{vin}")
    public ResponseEntity<Void> deleteCar(@PathVariable String vin) {
        carService.deleteCar(vin);
        return ResponseEntity.noContent().build();
    }
}
