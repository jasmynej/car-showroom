package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.OrderStatusRequest;
import com.slingshot.carshowroom.dto.PurchaseOrderRequest;
import com.slingshot.carshowroom.dto.PurchaseOrderResponse;
import com.slingshot.carshowroom.service.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping
    public List<PurchaseOrderResponse> getAllOrders() {
        return purchaseOrderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public PurchaseOrderResponse getOrder(@PathVariable Integer id) {
        return purchaseOrderService.getOrder(id);
    }

    @PostMapping
    public ResponseEntity<PurchaseOrderResponse> createOrder(@Valid @RequestBody PurchaseOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderService.createOrder(request));
    }

    @PatchMapping("/{id}/status")
    public PurchaseOrderResponse updateStatus(@PathVariable Integer id,
                                              @Valid @RequestBody OrderStatusRequest request) {
        return purchaseOrderService.updateStatus(id, request);
    }
}
