package com.example.repository;

import com.example.security.model.Authority;
import com.example.security.model.AuthorityName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByName(AuthorityName authorityName);
    //Authority findByAuthority(AuthorityName authorityName);

}
