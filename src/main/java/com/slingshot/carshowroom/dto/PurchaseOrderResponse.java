package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.OrderStatus;
import com.slingshot.carshowroom.model.PurchaseOrder;

import java.time.LocalDate;

public record PurchaseOrderResponse(
        Integer orderId,
        Integer customerId,
        String vin,
        LocalDate date,
        String comments,
        OrderStatus status
) {
    public static PurchaseOrderResponse from(PurchaseOrder order) {
        return new PurchaseOrderResponse(
                order.getOrderId(), order.getCustomerId(), order.getVin(),
                order.getDate(), order.getComments(), order.getStatus()
        );
    }
}
