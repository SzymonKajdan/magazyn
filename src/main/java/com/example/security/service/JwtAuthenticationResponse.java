package com.example.security.service;

import com.example.security.model.User;

import java.io.Serializable;
import java.util.Date;

public class JwtAuthenticationResponse implements Serializable {

    private static final long serialVersionUID = 1250166508152483573L;

    private final String token;

    private final Date expirationDate;

    private final User user;

    public JwtAuthenticationResponse(String token, Date expirationDate, User user) {
        this.token = token;
        this.expirationDate = expirationDate;
        this.user = user;
    }

    public String getToken() {
        return this.token;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public User getUser() {
        return user;
    }
}
