package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.Sale;

import java.time.LocalDate;

public record SaleResponse(
        Integer saleId,
        LocalDate saleDate,
        String vin
) {
    public static SaleResponse from(Sale sale) {
        return new SaleResponse(sale.getSaleId(), sale.getSaleDate(), sale.getVin());
    }
}
