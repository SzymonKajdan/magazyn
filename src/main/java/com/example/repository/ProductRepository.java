package com.example.repository;

import com.example.model.Principal;
import com.example.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByPriceOrderByPrice(double price);

    Product findByBarCode(String barcode);

    Product getProductById(Long id );

    Product findProductByBarCode(String barcode);
}
