package com.example.backend.Dto;

import java.util.UUID;

public class SendMessageRequest {
    private UUID chatid;
    private String message;
    private UUID Userid;

    public SendMessageRequest() {  }
    public SendMessageRequest(UUID chatid, String message, UUID Userid) {
        this.chatid = chatid;
        this.message = message;
        this.Userid = Userid;
    }

    public UUID getUserid() {
        return Userid;
    }

    public void setUserid(UUID userid) {
        Userid = userid;
    }

    public UUID getChatid() {
        return chatid;
    }

    public void setChatid(UUID chatid) {
        this.chatid = chatid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



}
