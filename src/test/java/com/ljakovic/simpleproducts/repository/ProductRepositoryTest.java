package com.ljakovic.simpleproducts.repository;

import com.ljakovic.simpleproducts.product.model.Product;
import com.ljakovic.simpleproducts.product.repo.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        final Long id = 1L;
        final String code = "1234567890";
        final String name = "Nikon Camera";
        final BigDecimal priceEur = BigDecimal.valueOf(1345.20);
        final BigDecimal priceUsd = BigDecimal.valueOf(1456.30);
        final boolean available = true;

        final Product product = new Product();
        product.setId(id);
        product.setCode(code);
        product.setName(name);
        product.setPriceEur(priceEur);
        product.setPriceUsd(priceUsd);
        product.setAvailable(available);

        productRepository.save(product);
    }

    @Test
    void testFindById() {
        final Long id = 1L;
        Optional<Product> product = productRepository.findById(id);
        Assertions.assertTrue(product.isPresent());
    }

    @Test
    void testFindByCode() {
        final String code = "1234567890";
        Optional<Product> product = productRepository.findByCode(code);
        Assertions.assertTrue(product.isPresent());
    }

    @Test
    void testFindAll() {
        List<Product> products = productRepository.findAll();
        assertNotNull(products);
        assertNotEquals(Collections.emptyList(), products);
    }
}
