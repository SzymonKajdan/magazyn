package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "LOCATION")
public class Location {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_seq")
    @SequenceGenerator(name = "location_seq", sequenceName = "location_seq", allocationSize = 1)
    private Long id;

    @Column(name = "BARCODELOCATION", length = 100, unique = true)
    @NotBlank
    @Size(min = 4, max = 100)
    private String barCodeLocation;

    @ManyToMany
    List<Product> products;

//    @Column(name = "AMOUNTOFPRODUCT")
//    @NotNull
//    private int amountOfProduct;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarCodeLocation() {
        return barCodeLocation;
    }

    public void setBarCodeLocation(String barCodeLocation) {
        this.barCodeLocation = barCodeLocation;
    }

//    public int getAmountOfProduct() {
//        return amountOfProduct;
//    }
//
//    public void setAmountOfProduct(int amountOfProduct) {
//        this.amountOfProduct = amountOfProduct;
//    }


    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> productList) {
        this.products = productList;
    }
}
