package com.example.security.service;

import java.io.Serializable;
import java.util.Date;

public class JwtAuthenticationResponse implements Serializable {

    private static final long serialVersionUID = 1250166508152483573L;

    private final String token;

    private final Date expirationDate;

    public JwtAuthenticationResponse(String token, Date expirationDate) {
        this.token = token;
        this.expirationDate = expirationDate;
    }

    public String getToken() {
        return this.token;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }
}
