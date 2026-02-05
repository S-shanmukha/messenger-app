package com.example.backend.Dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ChatResponseDto {
    private UUID id;
    private boolean groupChat;

    private String chatName;     // for group chat// optional

    private List<UserResponseDto> users;
    private List<UserResponseDto> admins;

    private MessageResponseDto lastMessage;  // important (for chat list UI)

    private LocalDateTime createdAt;

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

    public MessageResponseDto getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageResponseDto lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
