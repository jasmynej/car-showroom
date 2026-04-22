package com.slingshot.carshowroom.repository;

import com.slingshot.carshowroom.model.OrderStatus;
import com.slingshot.carshowroom.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {

    boolean existsByVinAndStatusIn(String vin, List<OrderStatus> statuses);
}
