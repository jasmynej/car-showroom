package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.PaymentRequest;
import com.slingshot.carshowroom.dto.PaymentResponse;
import com.slingshot.carshowroom.exception.ConflictException;
import com.slingshot.carshowroom.exception.ResourceNotFoundException;
import com.slingshot.carshowroom.model.*;
import com.slingshot.carshowroom.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PurchaseOrderRepository orderRepository;
    private final CarRepository carRepository;
    private final SaleRepository saleRepository;

    public PaymentService(PaymentRepository paymentRepository, InvoiceRepository invoiceRepository,
                          PurchaseOrderRepository orderRepository, CarRepository carRepository,
                          SaleRepository saleRepository) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.orderRepository = orderRepository;
        this.carRepository = carRepository;
        this.saleRepository = saleRepository;
    }

    public PaymentResponse getPayment(Integer id) {
        return PaymentResponse.from(findOrThrow(id));
    }

    @Transactional
    public PaymentResponse makePayment(PaymentRequest request) {
        if (paymentRepository.existsByInvoiceIdAndStatus(request.invoiceId(), PaymentStatus.PAID)) {
            throw new ConflictException("Invoice already paid: " + request.invoiceId());
        }

        Invoice invoice = invoiceRepository.findById(request.invoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + request.invoiceId()));

        PurchaseOrder order = orderRepository.findById(invoice.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found: " + invoice.getOrderId()));

        Car car = carRepository.findById(order.getVin())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + order.getVin()));

        Payment payment = new Payment();
        payment.setInvoiceId(request.invoiceId());
        payment.setCustomerId(request.customerId());
        payment.setVin(request.vin());
        payment.setAmount(request.amount());
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setAccountNumber(request.accountNumber());
        payment.setPin(request.pin());
        payment.setBank(request.bank());
        payment.setCreditCardNumber(request.creditCardNumber());
        payment.setCvvCode(request.cvvCode());
        Payment saved = paymentRepository.save(payment);

        car.setAvailabilityStatus(AvailabilityStatus.SOLD);
        car.setOwnerId(request.customerId());
        car.setLastUpdated(LocalDate.now());
        carRepository.save(car);

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        Sale sale = new Sale();
        sale.setVin(order.getVin());
        sale.setSaleDate(LocalDate.now());
        saleRepository.save(sale);

        return PaymentResponse.from(saved);
    }

    private Payment findOrThrow(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
    }
}
