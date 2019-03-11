package com.example.repository;

import com.example.model.Order;
import com.example.model.Principal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrincipalRepository extends JpaRepository<Principal, Long> {

    Principal findByNip(String nip);
}
