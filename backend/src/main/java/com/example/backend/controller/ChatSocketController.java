package com.example.backend.controller;

import com.example.backend.Dto.MessageResponseDto;
import com.example.backend.Dto.SendMessageRequest;
import com.example.backend.model.Message;
import com.example.backend.model.User;
import com.example.backend.service.MessageService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    @MessageMapping("/message")
    public void sendMessage(SendMessageRequest request) {

        User user=userService.finduserByemail(request.getEmail());
        Message message=messageService.sendMessage(request,user);

        MessageResponseDto dto = new MessageResponseDto();
        dto.setId(message.getId());
        dto.setMessage(message.getMessage());
        dto.setCreatedAt(message.getCreatedAt());

        if (message.getChat() != null) {
            dto.setChatId(message.getChat().getId());
        }

        if (message.getUser() != null) {
            dto.setSenderId(message.getUser().getId());
            dto.setSenderName(message.getUser().getName());
        }
        messagingTemplate.convertAndSend(
                "/topic/group/" + request.getChatid(),
                dto
        );
    }
}

