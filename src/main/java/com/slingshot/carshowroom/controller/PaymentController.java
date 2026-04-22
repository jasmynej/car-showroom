package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.PaymentRequest;
import com.slingshot.carshowroom.dto.PaymentResponse;
import com.slingshot.carshowroom.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{id}")
    public PaymentResponse getPayment(@PathVariable Integer id) {
        return paymentService.getPayment(id);
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> makePayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.makePayment(request));
    }
}
