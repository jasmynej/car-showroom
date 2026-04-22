package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.OrderStatusRequest;
import com.slingshot.carshowroom.dto.PurchaseOrderRequest;
import com.slingshot.carshowroom.dto.PurchaseOrderResponse;
import com.slingshot.carshowroom.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Purchase Orders", description = "Car purchase requests and approvals")
@RestController
@RequestMapping("/api/orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @Operation(summary = "List all purchase orders")
    @GetMapping
    public List<PurchaseOrderResponse> getAllOrders() {
        return purchaseOrderService.getAllOrders();
    }

    @Operation(summary = "Get a purchase order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public PurchaseOrderResponse getOrder(@PathVariable Integer id) {
        return purchaseOrderService.getOrder(id);
    }

    @Operation(summary = "Create a purchase order",
            description = "Car must exist and have `availabilityStatus = AVAILABLE`. Order is created with `status = PENDING`.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Car not found"),
            @ApiResponse(responseCode = "409", description = "Car is not available")
    })
    @PostMapping
    public ResponseEntity<PurchaseOrderResponse> createOrder(@Valid @RequestBody PurchaseOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderService.createOrder(request));
    }

    @Operation(summary = "Update order status",
            description = "Allowed transitions: PENDING → APPROVED (sets car to RESERVED), PENDING → REJECTED. " +
                    "COMPLETED is set automatically when payment is confirmed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{id}/status")
    public PurchaseOrderResponse updateStatus(@Parameter(description = "Order ID") @PathVariable Integer id,
                                              @Valid @RequestBody OrderStatusRequest request) {
        return purchaseOrderService.updateStatus(id, request);
    }
}
