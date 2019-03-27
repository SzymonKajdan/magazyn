package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "USEDPRODUCT")
public class UsedProduct {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usedproduct_seq")
    @SequenceGenerator(name = "usedproduct_seq", sequenceName = "usedproduct_seq", allocationSize = 1)
    private Long id;

    @Column(name = "quanitity")
    @NotNull
    private int quanitity;

    @Column(name = "IDPRODUCT")
    @NotNull
    private Long idproduct;

    @Column(name = "ISPICKED")
    private boolean isPicked;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuanitity() {
        return quanitity;
    }

    public void setQuanitity(int quanitity) {
        this.quanitity = quanitity;
    }

    public Long getIdproduct() {
        return idproduct;
    }

    public void setIdproduct(Long idproduct) {
        this.idproduct = idproduct;
    }

    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean picked) {
        isPicked = picked;
    }
}
