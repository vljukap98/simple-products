package com.ljakovic.simpleproducts.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
public class Product {

    @Id
    private Long id;
    @Size(min = 10, max = 10)
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private BigDecimal priceEur;
    private BigDecimal priceUsd;
    private Boolean isAvailable;

    public Product() {
    }

    public Product(Long id, String code, String name, BigDecimal priceEur, BigDecimal priceUsd, Boolean isAvailable) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.priceEur = priceEur;
        this.priceUsd = priceUsd;
        this.isAvailable = isAvailable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPriceEur() {
        return priceEur;
    }

    public void setPriceEur(BigDecimal priceEur) {
        this.priceEur = priceEur;
    }

    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(BigDecimal priceUsd) {
        this.priceUsd = priceUsd;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }
}
