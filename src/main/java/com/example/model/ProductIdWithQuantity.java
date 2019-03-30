package com.example.model;

public class ProductIdWithQuantity {

    private long id;
    private int quantity;

    public ProductIdWithQuantity() {
    }

    public ProductIdWithQuantity(long id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ProductIdWithQuantity{" +
                "id=" + id +
                ", quantity=" + quantity +
                '}';
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> szymonbranch
