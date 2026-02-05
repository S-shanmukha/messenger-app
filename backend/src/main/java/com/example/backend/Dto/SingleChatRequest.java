package com.example.backend.Dto;

import java.util.UUID;

public class SingleChatRequest {
    private UUID userid;

    public SingleChatRequest() {  }
    public SingleChatRequest(UUID userid) {
        this.userid = userid;
    }
    public UUID getUserid() {
        return userid;
    }

    public void setUserid(UUID userid) {
        this.userid = userid;
    }
}
