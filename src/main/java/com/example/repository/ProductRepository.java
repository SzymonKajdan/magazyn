package com.example.repository;

import com.example.model.Principal;
import com.example.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByOrderByPrice();

    List<Product> findAllByOrderByName();

    Product findByBarCode(String barcode);

    Product findProductByBarCode(String barcode);

    Boolean existsByBarCode(String barcode);
}
