package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "PRODUCT")
public class Product {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "product_seq", allocationSize = 1)
    private Long id;

    @Column(name = "BARCODE", length = 100, unique = true)
    @NotNull
    @Size(min = 4, max = 100)
    private String barCode;

    @OneToOne
    private Location location;

    @Column(name = "PRICE")
    @NotNull
    private Double price;

    @Column(name = "quantityOnThePalette")
    @NotNull
    private int quantityOnThePalette;

    @Column(name = "quanitity")
    @NotNull
    private int quanitity;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date exprDate;


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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getExprDate() {
        return exprDate;
    }

    public void setExprDate(Date exprDate) {
        this.exprDate = exprDate;
    }

    public int getQuanitity() {
        return quanitity;
    }

    public void setQuanitity(int quanitity) {
        this.quanitity = quanitity;
    }

    public int getQuantityOnThePalette() {
        return quantityOnThePalette;
    }

    public void setQuantityOnThePalette(int quantityOnThePalette) {
        this.quantityOnThePalette = quantityOnThePalette;
    }
}
