package com.ljakovic.simpleproducts.product.service;

import com.ljakovic.simpleproducts.product.dto.ProductDto;
import com.ljakovic.simpleproducts.product.mapper.ProductMapper;
import com.ljakovic.simpleproducts.product.model.Product;
import com.ljakovic.simpleproducts.product.repo.ProductRepository;
import com.ljakovic.simpleproducts.util.CurrencyCode;
import com.ljakovic.simpleproducts.util.PriceCalculator;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepo;
    private final ProductMapper productMapper;
    private final PriceCalculator priceCalculator;

    @Autowired
    public ProductService(ProductRepository productRepo, ProductMapper productMapper, PriceCalculator priceCalculator) {
        this.productRepo = productRepo;
        this.productMapper = productMapper;
        this.priceCalculator = priceCalculator;
    }

    public Product getProduct(final String code) {
        return productRepo.findByCode(code)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product with code: '" + code + "' not found")
                );
    }

    public Page<Product> getProducts(final Pageable pageable) {
        return productRepo.findAll(pageable);
    }

    public ProductDto getProductDto(final String code) {
        final Product product = getProduct(code);
        return productMapper.mapTo(product);
    }

    public Page<ProductDto> getProductDtos(final Pageable pageable) {
        final Page<Product> products = getProducts(pageable);

        final List<ProductDto> productDtoList = products.stream()
                .map(productMapper::mapTo)
                .toList();

        return new PageImpl<>(productDtoList, products.getPageable(), products.getTotalElements());
    }

    public ProductDto createProduct(final ProductDto dto) {
        if (Boolean.TRUE.equals(productRepo.existsByCode(dto.getCode()))) {
            LOG.info("Product with code: '{}' already exists", dto.getCode());
            throw new DataIntegrityViolationException("Product code not unique");
        }

        Product product = new Product();

        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setPriceEur(dto.getPriceEur());

        if (dto.isAvailable() != null) {
            product.setAvailable(dto.isAvailable());
        } else {
            product.setAvailable(true);
        }

        final BigDecimal priceUsd = priceCalculator.calculatePrice(dto.getPriceEur(), CurrencyCode.USD_CURRENCY);
        product.setPriceUsd(priceUsd);

        productRepo.save(product);

        return productMapper.mapTo(product);
    }
}
