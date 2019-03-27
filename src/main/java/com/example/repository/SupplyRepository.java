package com.example.repository;

import com.example.model.Product;
import com.example.model.Supply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplyRepository extends JpaRepository<Supply, Long> {
    Supply findByBarCodeOfSupply(String barcode);
    List<Supply>findByStatus(boolean status);
}
