package com.project.security.entities;

import java.io.Serializable;

public class ActivationToken implements Serializable {
    public String getToken() {
        return token;
    }

    private String token = "";

    ActivationToken(String token) {
        this.token = token;
    }

    ActivationToken() {
    }
}
