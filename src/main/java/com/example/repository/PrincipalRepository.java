package com.example.repository;

import com.example.model.Order;
import com.example.model.Principal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PrincipalRepository extends JpaRepository<Principal, Long> {

    Principal findByNip(String nip);

    List<Principal> findAllByOrderByCompanyName();

    List<Principal> findAllByEnabled(Boolean enabled);

    List<Principal> findAllByEnabledOrderByCompanyName(Boolean enabled);

    void deleteByNip(String nip);

    Boolean existsByNip(String nip);
}
