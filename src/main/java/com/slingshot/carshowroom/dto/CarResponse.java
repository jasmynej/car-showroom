package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.AvailabilityStatus;
import com.slingshot.carshowroom.model.Car;

import java.time.LocalDate;

public record CarResponse(
        String vin,
        String make,
        String model,
        Integer year,
        Double price,
        String color,
        Double mileage,
        AvailabilityStatus availabilityStatus,
        LocalDate lastServiceDate,
        Integer ownerId,
        LocalDate lastUpdated
) {
    public static CarResponse from(Car car) {
        return new CarResponse(
                car.getVin(), car.getMake(), car.getModel(), car.getYear(),
                car.getPrice(), car.getColor(), car.getMileage(),
                car.getAvailabilityStatus(), car.getLastServiceDate(),
                car.getOwnerId(), car.getLastUpdated()
        );
    }
}
