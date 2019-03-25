package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.JSONPropertyIgnore;
import org.springframework.lang.Nullable;

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

    @Column(name = "BARCODE", length = 100, unique = false)
    @NotNull
    @Size(min = 4, max = 100)
    private String barCode;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date exprDate;

    private int state;

    @ManyToMany
    private List<Location> locations;

    @ManyToOne
    private StaticProduct staticProduct;

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

    public Date getExprDate() {
        return exprDate;
    }

    public void setExprDate(Date exprDate) {
        this.exprDate = exprDate;
    }

    @JsonIgnore
    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locationList) {
        this.locations = locationList;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public StaticProduct getStaticProduct() {
        return staticProduct;
    }

    public void setStaticProduct(StaticProduct staticProduct) {
        this.staticProduct = staticProduct;
    }
}
