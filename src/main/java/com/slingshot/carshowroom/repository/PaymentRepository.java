package com.slingshot.carshowroom.repository;

import com.slingshot.carshowroom.model.Payment;
import com.slingshot.carshowroom.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    boolean existsByInvoiceIdAndStatus(Integer invoiceId, PaymentStatus status);
}
