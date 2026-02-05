package com.example.backend.Dto;

import java.util.List;
import java.util.UUID;

public class ChatDetailsResponseDto {
    private UUID id;
    private boolean groupChat;

    private String chatName;

    private List<UserResponseDto> users;
    private List<UserResponseDto> admins;

    private List<MessageResponseDto> messages;

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

    public List<UserResponseDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserResponseDto> users) {
        this.users = users;
    }

    public List<UserResponseDto> getAdmins() {
        return admins;
    }

    public void setAdmins(List<UserResponseDto> admins) {
        this.admins = admins;
    }

    public List<MessageResponseDto> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageResponseDto> messages) {
        this.messages = messages;
    }
}
