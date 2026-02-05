package com.example.backend.Dto;

import java.util.UUID;

public class UpdateUserRequest {
    private UUID userid;
    private String name;
    private String password;

    public UpdateUserRequest(UUID userid) {}
    public UpdateUserRequest(UUID userid, String name, String password) {
        this.userid = userid;
        this.name = name;
        this.password = password;
    }

    public UUID getUserid() {
        return userid;
    }

    public void setUserid(UUID userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
