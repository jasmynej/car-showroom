package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.Payment;
import com.slingshot.carshowroom.model.PaymentMethod;
import com.slingshot.carshowroom.model.PaymentStatus;

import java.time.LocalDate;

public record PaymentResponse(
        Integer transactionId,
        Integer invoiceId,
        Integer customerId,
        String vin,
        Double amount,
        PaymentStatus status,
        LocalDate paymentDate,
        PaymentMethod paymentMethod
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getTransactionId(), payment.getInvoiceId(), payment.getCustomerId(),
                payment.getVin(), payment.getAmount(), payment.getStatus(),
                payment.getPaymentDate(), payment.getPaymentMethod()
        );
    }
}
