package com.example.backend.Dto;

import java.util.List;
import java.util.UUID;

public class GroupChatRequest {
    List<UUID> userids;
    String Chatname;
    public GroupChatRequest(){}
    public List<UUID> getUserids() {
        return userids;
    }

    public void setUserids(List<UUID> userids) {
        this.userids = userids;
    }

    public List<UUID> getChatids() {
        return userids;
    }

    public String getChatName() {
        return Chatname;
    }

    public void setChatName(String chatname) {
        Chatname = chatname;
    }

    public GroupChatRequest(List<UUID> userids, String Chatname) {
        this.userids = userids;
        this.Chatname = Chatname;
    }


}
