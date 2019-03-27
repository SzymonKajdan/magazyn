package com.example.repository;

import com.example.model.Product;
import com.example.model.Supply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplyRepository extends JpaRepository<Supply, Long> {
    Supply findByBarCodeOfSupply(String barcode);
}
