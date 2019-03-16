package com.example.repository;

import com.example.model.Location;
import com.example.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByBarCodeLocation(String barCodeLocation);
}
