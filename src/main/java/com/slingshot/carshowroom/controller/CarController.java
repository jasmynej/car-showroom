package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.CarRequest;
import com.slingshot.carshowroom.dto.CarResponse;
import com.slingshot.carshowroom.service.CarService;
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

@Tag(name = "Cars", description = "Inventory management")
@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @Operation(summary = "List all cars")
    @GetMapping
    public List<CarResponse> getAllCars() {
        return carService.getAllCars();
    }

    @Operation(summary = "List available cars", description = "Returns only cars with availabilityStatus = AVAILABLE")
    @GetMapping("/available")
    public List<CarResponse> getAvailableCars() {
        return carService.getAvailableCars();
    }

    @Operation(summary = "Get a car by VIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Car found"),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @GetMapping("/{vin}")
    public CarResponse getCar(@Parameter(description = "17-character VIN") @PathVariable String vin) {
        return carService.getCar(vin);
    }

    @Operation(summary = "Add a new car")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Car created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "VIN already exists")
    })
    @PostMapping
    public ResponseEntity<CarResponse> addCar(@Valid @RequestBody CarRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.addCar(request));
    }

    @Operation(summary = "Update a car", description = "VIN is immutable. Only provided fields are updated.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Car updated"),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @PutMapping("/{vin}")
    public CarResponse updateCar(@Parameter(description = "17-character VIN") @PathVariable String vin,
                                 @RequestBody CarRequest request) {
        return carService.updateCar(vin, request);
    }

    @Operation(summary = "Delete a car")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Car deleted"),
            @ApiResponse(responseCode = "404", description = "Car not found"),
            @ApiResponse(responseCode = "409", description = "Car has a PENDING or APPROVED purchase order")
    })
    @DeleteMapping("/{vin}")
    public ResponseEntity<Void> deleteCar(@Parameter(description = "17-character VIN") @PathVariable String vin) {
        carService.deleteCar(vin);
        return ResponseEntity.noContent().build();
    }
}
