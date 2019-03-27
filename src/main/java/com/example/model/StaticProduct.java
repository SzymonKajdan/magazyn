package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "STATICPRODUCT")
public class StaticProduct {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "staticproduct_seq")
    @SequenceGenerator(name = "staticproduct_seq", sequenceName = "staticproduct_seq", allocationSize = 1)
    private Long id;

    @Column(name = "PRICE")
    @NotNull
    private Double price;

    @Column(name = "quantityOnThePalette")
    @NotNull
    private int quantityOnThePalette;

    @ManyToMany
    private List<StaticLocation> staticLocations;

    @OneToMany
    private List<Product> products;

    private String producer;

    private String name;

    private String category;

    private int logicState;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public int getLogicState() {
        return logicState;
    }

    public void setLogicState(int logicState) {
        this.logicState = logicState;
    }
}
