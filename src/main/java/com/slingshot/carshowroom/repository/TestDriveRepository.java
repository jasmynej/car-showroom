package com.slingshot.carshowroom.repository;

import com.slingshot.carshowroom.model.TestDrive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestDriveRepository extends JpaRepository<TestDrive, Integer> {
}
