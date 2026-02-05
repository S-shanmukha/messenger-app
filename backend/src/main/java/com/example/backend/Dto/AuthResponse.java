package com.example.backend.Dto;

public class AuthResponse {
    private String jwt;
    private Boolean status;

    public AuthResponse() {}
    public AuthResponse(String jwt, Boolean status) {
        this.jwt = jwt;
        this.status = status;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }



}
