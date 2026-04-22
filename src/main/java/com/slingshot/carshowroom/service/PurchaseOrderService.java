package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.OrderStatusRequest;
import com.slingshot.carshowroom.dto.PurchaseOrderRequest;
import com.slingshot.carshowroom.dto.PurchaseOrderResponse;
import com.slingshot.carshowroom.exception.ConflictException;
import com.slingshot.carshowroom.exception.ResourceNotFoundException;
import com.slingshot.carshowroom.model.AvailabilityStatus;
import com.slingshot.carshowroom.model.Car;
import com.slingshot.carshowroom.model.OrderStatus;
import com.slingshot.carshowroom.model.PurchaseOrder;
import com.slingshot.carshowroom.repository.CarRepository;
import com.slingshot.carshowroom.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository orderRepository;
    private final CarRepository carRepository;

    public PurchaseOrderService(PurchaseOrderRepository orderRepository, CarRepository carRepository) {
        this.orderRepository = orderRepository;
        this.carRepository = carRepository;
    }

    public List<PurchaseOrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(PurchaseOrderResponse::from).toList();
    }

    public PurchaseOrderResponse getOrder(Integer id) {
        return PurchaseOrderResponse.from(findOrThrow(id));
    }

    public PurchaseOrderResponse createOrder(PurchaseOrderRequest request) {
        Car car = carRepository.findById(request.vin())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + request.vin()));
        if (car.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
            throw new ConflictException("Car is not available for purchase: " + request.vin());
        }
        PurchaseOrder order = new PurchaseOrder();
        order.setCustomerId(request.customerId());
        order.setVin(request.vin());
        order.setDate(LocalDate.now());
        order.setComments(request.comments());
        order.setStatus(OrderStatus.PENDING);
        return PurchaseOrderResponse.from(orderRepository.save(order));
    }

    public PurchaseOrderResponse updateStatus(Integer id, OrderStatusRequest request) {
        PurchaseOrder order = findOrThrow(id);
        order.setStatus(request.status());
        if (request.status() == OrderStatus.APPROVED) {
            carRepository.findById(order.getVin()).ifPresent(car -> {
                car.setAvailabilityStatus(AvailabilityStatus.RESERVED);
                carRepository.save(car);
            });
        }
        return PurchaseOrderResponse.from(orderRepository.save(order));
    }

    private PurchaseOrder findOrThrow(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found: " + id));
    }
}
