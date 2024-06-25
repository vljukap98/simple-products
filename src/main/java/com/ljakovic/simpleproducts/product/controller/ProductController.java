package com.ljakovic.simpleproducts.product.controller;

import com.ljakovic.simpleproducts.product.dto.ProductDto;
import com.ljakovic.simpleproducts.product.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ljakovic.simpleproducts.util.SortPropertyUtil.PROPERTY_NAME;


@RestController
@RequestMapping("/v1/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<ProductDto>> getProducts(
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page
    ) {
        Sort sort = Sort.by(Sort.Direction.ASC, PROPERTY_NAME);
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : page, size, sort);

        return ResponseEntity.ok(productService.getProducts(pageable));
    }

    @PostMapping(
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.createProduct(dto));
    }
}
