package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.InvoiceRequest;
import com.slingshot.carshowroom.dto.InvoiceResponse;
import com.slingshot.carshowroom.exception.ConflictException;
import com.slingshot.carshowroom.exception.ResourceNotFoundException;
import com.slingshot.carshowroom.model.Invoice;
import com.slingshot.carshowroom.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public InvoiceResponse getInvoice(Integer id) {
        return InvoiceResponse.from(findOrThrow(id));
    }

    public InvoiceResponse createInvoice(InvoiceRequest request) {
        if (invoiceRepository.existsByOrderId(request.orderId())) {
            throw new ConflictException("Invoice already exists for order: " + request.orderId());
        }
        Invoice invoice = new Invoice();
        invoice.setOrderId(request.orderId());
        invoice.setCustomerId(request.customerId());
        invoice.setPrice(request.price());
        invoice.setTax(request.tax());
        invoice.setTotalAmount(request.price() + (request.price() * request.tax() / 100));
        invoice.setDate(LocalDate.now());
        invoice.setTermsAndConditions(request.termsAndConditions());
        return InvoiceResponse.from(invoiceRepository.save(invoice));
    }

    private Invoice findOrThrow(Integer id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + id));
    }
}
