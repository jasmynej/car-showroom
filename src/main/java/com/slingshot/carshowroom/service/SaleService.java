package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.SaleResponse;
import com.slingshot.carshowroom.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;

    public SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public List<SaleResponse> getAllSales() {
        return saleRepository.findAll().stream().map(SaleResponse::from).toList();
    }
}
