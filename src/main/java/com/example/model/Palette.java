package com.example.model;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "PALETTE")
public class Palette {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "palette_seq")
    @SequenceGenerator(name = "palette_seq", sequenceName = "palette_seq", allocationSize = 1)
    private Long id;

    @Column(name = "BARCODE")
    private String barCode;

    @ManyToMany
    private List<UsedProduct> usedProducts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public List<UsedProduct> getUsedProducts() {
        return usedProducts;
    }

    public void setUsedProducts(List<UsedProduct> usedProducts) {
        this.usedProducts = usedProducts;
    }
}




