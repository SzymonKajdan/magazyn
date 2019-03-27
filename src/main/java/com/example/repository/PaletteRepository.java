package com.example.repository;

import com.example.model.Palette;
import com.example.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaletteRepository extends JpaRepository<Palette, Long> {
    Palette findByBarCode(String barCode);
}
