package com.example.repository;

import com.example.model.Location;
import com.example.model.Product;
import com.example.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByBarCodeLocation(String barCodeLocation);


}
