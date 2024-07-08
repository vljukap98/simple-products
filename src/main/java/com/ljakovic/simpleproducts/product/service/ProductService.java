package com.ljakovic.simpleproducts.product.service;

import com.ljakovic.simpleproducts.exception.NonUniqueProductCodeException;
import com.ljakovic.simpleproducts.product.dto.ProductDto;
import com.ljakovic.simpleproducts.product.mapper.ProductMapper;
import com.ljakovic.simpleproducts.product.model.Product;
import com.ljakovic.simpleproducts.product.repo.ProductRepository;
import com.ljakovic.simpleproducts.util.CurrencyCode;
import com.ljakovic.simpleproducts.util.PriceCalculator;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final PriceCalculator priceCalculator;

    public ProductService(ProductRepository productRepo, PriceCalculator priceCalculator) {
        this.productRepo = productRepo;
        this.priceCalculator = priceCalculator;
    }

    /**
     * Retrieves a product by its code
     * If the product is not found, an EntityNotFoundException is thrown
     *
     * @param code The code of the product to retrieve
     * @return The product with the specified code
     * @throws EntityNotFoundException If no product with the given code is found
     */
    public Product getProduct(final String code) {
        return productRepo.findByCode(code)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product with code: '" + code + "' not found")
                );
    }

    /**
     * Retrieves a paginated list of products
     *
     * @param pageable The pagination information
     * @return A paginated list of products
     */
    public Page<Product> getProducts(final Pageable pageable) {
        return productRepo.findAll(pageable);
    }

    /**
     * Retrieves a product by its code and maps it to a ProductDto
     *
     * @param code The code of the product to retrieve
     * @return The ProductDto corresponding to the product with the specified code
     * @throws EntityNotFoundException If no product with the given code is found
     */
    public ProductDto getProductDto(final String code) {
        final Product product = getProduct(code);
        return ProductMapper.mapTo(product);
    }

    /**
     * Retrieves a paginated list of products and maps them to ProductDtos
     *
     * @param pageable The pagination information
     * @return A paginated list of ProductDtos
     */
    public Page<ProductDto> getProductDtos(final Pageable pageable) {
        final Page<Product> products = getProducts(pageable);

        final List<ProductDto> productDtoList = products.stream()
                .map(ProductMapper::mapTo)
                .toList();

        return new PageImpl<>(productDtoList, products.getPageable(), products.getTotalElements());
    }

    /**
     * Creates a new product based on the provided ProductDto
     * If a product with the same code already exists, a DataIntegrityViolationException is thrown
     *
     * @param dto The ProductDto containing the information for the new product
     * @return The created ProductDto
     * @throws DataIntegrityViolationException If a product with the same code already exists
     */
    public ProductDto createProduct(final ProductDto dto) {
        if (Boolean.TRUE.equals(productRepo.existsByCode(dto.getCode()))) {
            LOG.info("Product with code: '{}' already exists", dto.getCode());
            throw new NonUniqueProductCodeException("Product code not unique");
        }

        Product product = new Product();

        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setPriceEur(dto.getPriceEur());

        if (dto.getAvailable() != null) {
            product.setAvailable(dto.getAvailable());
        } else {
            product.setAvailable(true);
        }

        final BigDecimal priceUsd;
        if (dto.getPriceUsd() != null) {
            priceUsd = dto.getPriceUsd();
        } else {
            priceUsd = priceCalculator.calculatePrice(dto.getPriceEur(), CurrencyCode.USD_CURRENCY);
        }
        product.setPriceUsd(priceUsd);

        productRepo.save(product);

        return ProductMapper.mapTo(product);
    }
}
