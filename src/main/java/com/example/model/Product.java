package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

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

    @OneToMany
    private List<Location> location;

    @Column(name = "PRICE")
    @NotNull
    private Double price;

    @Column(name = "quantityOnThePalette")
    @NotNull
    private int quantityOnThePalette;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date exprDate;

    private String producer;

    private int logicState;

    private  String name;

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


    public List<Location> getLocation() {
        return location;
    }

    public void setLocation(List<Location> location) {
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


    public int getQuantityOnThePalette() {
        return quantityOnThePalette;
    }

    public void setQuantityOnThePalette(int quantityOnThePalette) {
        this.quantityOnThePalette = quantityOnThePalette;
    }



    public int getLogicState() {
        return logicState;
    }

    public void setLogicState(int logicState) {
        this.logicState = logicState;
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
}
