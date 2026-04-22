package com.slingshot.carshowroom.repository;

import com.slingshot.carshowroom.model.ServiceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceScheduleRepository extends JpaRepository<ServiceSchedule, Integer> {
}
