package com.slingshot.carshowroom.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "sale")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer saleId;

    @Column(nullable = false)
    private LocalDate saleDate;

    @Column(nullable = false, length = 17)
    private String vin;

    public Sale() {}

    public Integer getSaleId() { return saleId; }
    public void setSaleId(Integer saleId) { this.saleId = saleId; }

    public LocalDate getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
}
