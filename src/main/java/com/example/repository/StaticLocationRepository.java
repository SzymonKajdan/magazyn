package com.example.repository;

import com.example.model.StaticLocation;
import com.example.model.StaticProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticLocationRepository extends JpaRepository<StaticLocation, Long> {
    StaticLocation findByBarCodeLocation(String barCode);
}
