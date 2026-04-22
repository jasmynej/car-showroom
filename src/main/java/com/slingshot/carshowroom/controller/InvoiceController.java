package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.InvoiceRequest;
import com.slingshot.carshowroom.dto.InvoiceResponse;
import com.slingshot.carshowroom.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{id}")
    public InvoiceResponse getInvoice(@PathVariable Integer id) {
        return invoiceService.getInvoice(id);
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(request));
    }
}
