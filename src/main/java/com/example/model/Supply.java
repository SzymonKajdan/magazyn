package com.example.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "SUPPLY")
public class Supply {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supply_seq")
    @SequenceGenerator(name = "supply_seq", sequenceName = "supply_seq", allocationSize = 1)
    private Long id;

    @Column(name = "TYPEOFSUPPLY")
    private  String typeOfSupply;

    @Column(name = "BARCODEOFSUPPLY")
    private  String barCodeOfSupply;

    @OneToMany
    private List<Palette> paletteList;

    private Date arriveDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeOfSupply() {
        return typeOfSupply;
    }

    public void setTypeOfSupply(String typeOfSupply) {
        this.typeOfSupply = typeOfSupply;
    }

    public String getBarCodeOfSupply() {
        return barCodeOfSupply;
    }

    public void setBarCodeOfSupply(String barCodeOfSupply) {
        this.barCodeOfSupply = barCodeOfSupply;
    }

    public List<Palette> getPaletteList() {
        return paletteList;
    }

    public void setPaletteList(List<Palette> paletteList) {
        this.paletteList = paletteList;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }
}
