package com.example.repository;

import com.example.model.UsedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsedProductRepository extends JpaRepository<UsedProduct, Long> {
}
