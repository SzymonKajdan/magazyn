package com.example.model;

import javax.persistence.*;
@Entity
@Table(name = "STATICLOCATIONS")
public class StaticLocation {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "staticproduct_seq")
    @SequenceGenerator(name = "staticproduct_seq", sequenceName = "staticproduct_seq", allocationSize = 1)
    private Long id;
    private String barCodeLocation;

    public String getBarCodeLocation() {
        return barCodeLocation;
    }

    public void setBarCodeLocation(String barCodeLocation) {
        this.barCodeLocation = barCodeLocation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
