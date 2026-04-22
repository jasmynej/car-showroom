package com.slingshot.carshowroom.repository;

import com.slingshot.carshowroom.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    boolean existsByOrderId(Integer orderId);

    Optional<Invoice> findByOrderId(Integer orderId);
}
