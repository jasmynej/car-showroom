package com.slingshot.carshowroom.repository;

import com.slingshot.carshowroom.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Integer> {
}
