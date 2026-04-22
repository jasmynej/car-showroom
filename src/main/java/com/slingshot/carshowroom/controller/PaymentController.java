package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.PaymentRequest;
import com.slingshot.carshowroom.dto.PaymentResponse;
import com.slingshot.carshowroom.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payments", description = "Payment processing — triggers sale completion")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Get a payment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/{id}")
    public PaymentResponse getPayment(@PathVariable Integer id) {
        return paymentService.getPayment(id);
    }

    @Operation(summary = "Make a payment",
            description = """
                    Processes payment for an invoice. On success (atomic transaction):
                    - Payment status set to `PAID`
                    - Car `availabilityStatus` set to `SOLD`, `ownerId` set to customer
                    - Purchase order `status` set to `COMPLETED`
                    - A `Sale` record is created

                    Supply `accountNumber`, `pin`, and `bank` for `CASH` payments.
                    Supply `creditCardNumber` and `cvvCode` for `CREDIT_CARD` payments.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment processed"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Invoice or related record not found"),
            @ApiResponse(responseCode = "409", description = "Invoice already paid")
    })
    @PostMapping
    public ResponseEntity<PaymentResponse> makePayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.makePayment(request));
    }
}
