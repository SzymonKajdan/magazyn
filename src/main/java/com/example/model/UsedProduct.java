package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "USEDPRODUCT")
public class UsedProduct {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usedproduct_seq")
    @SequenceGenerator(name = "usedproduct_seq", sequenceName = "usedproduct_seq", allocationSize = 1)
    private Long id;

    @Column(name = "quanitity")
    @NotNull
    private int quanitity;

    @Column(name = "BARCODE")

    private String barCodeProduct;

    @Column(name = "ISPICKED")
    private boolean isPicked;

    private Long idStaticProduct;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuanitity() {
        return quanitity;
    }

    public void setQuanitity(int quanitity) {
        this.quanitity = quanitity;
    }


    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean picked) {
        isPicked = picked;
    }

    public String getBarCodeProduct() {
        return barCodeProduct;
    }

    public void setBarCodeProduct(String barCodeProduct) {
        this.barCodeProduct = barCodeProduct;
    }

    public Long getIdStaticProduct() {
        return idStaticProduct;
    }

    public void setIdStaticProduct(Long idStaticProduct) {
        this.idStaticProduct = idStaticProduct;
    }
}
