package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "USEDPRODUCTLOT")
public class UsedProductLot {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usedproductlot_seq")
    @SequenceGenerator(name = "usedproductlot_seq", sequenceName = "usedproductlot_seq", allocationSize = 1)
    private Long id;

    @Column(name = "productID")
    @NotNull
    private Long productID;

    @Column(name = "quanitity")
    @NotNull
    private int quanitity;

    public Long getProductID() {
        return productID;
    }

    public void setProductID(Long productID) {
        this.productID = productID;
    }

    public int getQuanitity() {
        return quanitity;
    }

    public void setQuanitity(int quanitity) {
        this.quanitity = quanitity;
    }

    public Long getId() {
        return id;
    }
}
