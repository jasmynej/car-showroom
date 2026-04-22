package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.Invoice;

import java.time.LocalDate;

public record InvoiceResponse(
        Integer invoiceId,
        Integer orderId,
        Integer customerId,
        Double price,
        Double tax,
        Double totalAmount,
        LocalDate date,
        String termsAndConditions
) {
    public static InvoiceResponse from(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getInvoiceId(), invoice.getOrderId(), invoice.getCustomerId(),
                invoice.getPrice(), invoice.getTax(), invoice.getTotalAmount(),
                invoice.getDate(), invoice.getTermsAndConditions()
        );
    }
}
