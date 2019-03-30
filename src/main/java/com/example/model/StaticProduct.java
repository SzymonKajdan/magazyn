package com.example.model;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "STATICPRODUCTLIST")
public class StaticProduct {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "staticlocation_seq")
    @SequenceGenerator(name = "staticlocation_seq", sequenceName = "staticlocation_seq", allocationSize = 1)
    private Long id;


    @Column(name = "PRICE")
    @NotNull
    private Double price;

    @Column(name = "quantityOnThePalette")
    @NotNull
    private int quantityOnThePalette;
    @ColumnDefault(value = "1")
    private int amountInAPack;

    private String producer;

    @Column(name = "BARCODE", length = 100)
    @NotNull
    @Size(min = 4, max = 100)
    private String barCode;

    private String name;

    private int logicState;

    private String category;
    @OneToMany
    private List<Product> products;

    @ManyToMany
    private List<StaticLocation> staticLocations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantityOnThePalette() {
        return quantityOnThePalette;
    }

    public void setQuantityOnThePalette(int quantityOnThePalette) {
        this.quantityOnThePalette = quantityOnThePalette;
    }

    public List<StaticLocation> getStaticLocations() {
        return staticLocations;
    }

    public void setStaticLocations(List<StaticLocation> staticLocations) {
        this.staticLocations = staticLocations;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public int getLogicState() {
        return logicState;
    }

    public void setLogicState(int logicState) {
        this.logicState = logicState;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }



    public int getAmountInAPack() {
        return amountInAPack;
    }
}