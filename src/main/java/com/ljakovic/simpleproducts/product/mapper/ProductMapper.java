package com.ljakovic.simpleproducts.product.mapper;

import com.ljakovic.simpleproducts.product.dto.ProductDto;
import com.ljakovic.simpleproducts.product.model.Product;

public class ProductMapper {

    private ProductMapper() {}

    public static ProductDto mapTo(Product product) {
        ProductDto dto = new ProductDto();

        dto.setId(product.getId());
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setAvailable(product.getAvailable());

        if (product.getPriceEur() != null) {
            dto.setPriceEur(product.getPriceEur());
        }

        if (product.getPriceUsd() != null) {
            dto.setPriceUsd(product.getPriceUsd());
        }

        return dto;
    }
}
