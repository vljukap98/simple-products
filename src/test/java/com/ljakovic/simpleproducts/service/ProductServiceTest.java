package com.ljakovic.simpleproducts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import com.ljakovic.simpleproducts.product.model.Product;
import com.ljakovic.simpleproducts.product.repo.ProductRepository;
import com.ljakovic.simpleproducts.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void testGetProductByCode() {
        final String code = "1234567890";
        final String name = "Nikon Camera";
        final BigDecimal priceEur = BigDecimal.valueOf(1345.20);
        final BigDecimal priceUsd = BigDecimal.valueOf(1456.30);
        final boolean available = true;

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setCode(code);
        mockProduct.setName(name);
        mockProduct.setPriceEur(priceEur);
        mockProduct.setPriceUsd(priceUsd);
        mockProduct.setAvailable(available);

        when(productRepository.findByCode(code)).thenReturn(Optional.of(mockProduct));

        Product result = productService.getProduct(code);

        assertNotNull(result);
        assertEquals(code, result.getCode());
        assertEquals(name, result.getName());
        assertEquals(priceEur, result.getPriceEur());
        assertEquals(priceUsd, result.getPriceUsd());
        assertEquals(available, result.getAvailable());
    }
}

