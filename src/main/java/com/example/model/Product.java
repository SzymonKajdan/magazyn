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



    @Temporal(TemporalType.TIMESTAMP)

    private Date exprDate;

    private int state;

    @ManyToMany
    @JoinTable(
            name = "PRODUCT_LOCATION",
            joinColumns = {@JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "LOCATION_ID", referencedColumnName = "ID")})
    private List<Location> locations;

    @ManyToOne
    private StaticProduct staticProduct;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    @JsonIgnore
    public StaticProduct getStaticProduct() {
        return staticProduct;
    }

    public void setStaticProduct(StaticProduct staticProduct) {
        this.staticProduct = staticProduct;
    }
}