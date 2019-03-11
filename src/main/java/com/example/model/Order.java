package com.example.model;

import com.example.security.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity(name = "ORDERS")
@Table(name = "ORDERS")
public class Order {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "order_seq", allocationSize = 1)
    private Long id;

    @OneToOne
    private Product product;

    @OneToOne
    private User user;

    @OneToOne
    private Principal principal;

    @NotNull
    private Double price;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date date;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
