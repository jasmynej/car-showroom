package com.slingshot.carshowroom.controller;

import com.slingshot.carshowroom.dto.SaleResponse;
import com.slingshot.carshowroom.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Sales", description = "Completed sale records (read-only)")
@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @Operation(summary = "List all sales", description = "Sale records are created automatically when a payment is marked PAID.")
    @GetMapping
    public List<SaleResponse> getAllSales() {
        return saleService.getAllSales();
    }
}
