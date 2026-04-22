package com.slingshot.carshowroom.repository;

import com.slingshot.carshowroom.model.AvailabilityStatus;
import com.slingshot.carshowroom.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, String> {

    List<Car> findByAvailabilityStatus(AvailabilityStatus status);

    List<Car> findByOwnerId(Integer ownerId);
}
