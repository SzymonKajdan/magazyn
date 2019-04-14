package com.example.model;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "PRINCIPAL")
public class Principal {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "principal_seq")
    @SequenceGenerator(name = "principal_seq", sequenceName = "principal_seq", allocationSize = 1)
    private Long id;

    @Column(name = "COMPANYNAME", length = 50)
    @Size(min = 4, max = 50)
    private String companyName;

    @Column(name = "NIP", length = 100)
    @NotNull
    @Size(min = 4, max = 100)
    private String nip;

    @Column(name = "ZIPCODE", length = 50)
    @Size(min = 4, max = 50)
    private String zipCode;

    @Column(name = "ADDRESS", length = 50)
    @Size(min = 4, max = 50)
    private String address;

    @Column(name = "PHONENO", length = 50)
    @Size(min = 4, max = 50)
    private String phoneNo;

    @Column(name = "ENABLED")//, columnDefinition = "BOOLEAN DEFAULT true")
    //@ColumnDefault(value = "1")
    private Boolean enabled = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
