package com.ljakovic.simpleproducts.repo;

import com.ljakovic.simpleproducts.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
