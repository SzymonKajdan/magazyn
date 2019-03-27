package com.example.repository;

import com.example.model.StaticLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticLocationRepository extends JpaRepository<StaticLocation, Long> {
}
