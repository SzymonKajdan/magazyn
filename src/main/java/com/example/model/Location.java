package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    @Column(name = "AMOUNTOFPRODUCT")
    @NotNull
    private Double amountOfProduct;

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

    public Double getAmountOfProduct() {
        return amountOfProduct;
    }

    public void setAmountOfProduct(Double amountOfProduct) {
        this.amountOfProduct = amountOfProduct;
    }
}
