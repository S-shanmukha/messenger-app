package com.example.backend.Dto;

import java.util.UUID;

public class ChatListResponseDto {
    private UUID id;
    private boolean groupChat;

    private String chatName;
    private String chatImage;

    private MessageResponseDto lastMessage;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isGroupChat() {
        return groupChat;
    }

    public void setGroupChat(boolean groupChat) {
        this.groupChat = groupChat;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatImage() {
        return chatImage;
    }

    public void setChatImage(String chatImage) {
        this.chatImage = chatImage;
    }

    public MessageResponseDto getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageResponseDto lastMessage) {
        this.lastMessage = lastMessage;
    }

}
