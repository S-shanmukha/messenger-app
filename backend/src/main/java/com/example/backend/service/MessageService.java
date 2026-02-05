package com.example.backend.service;

import com.example.backend.Dto.SendMessageRequest;
import com.example.backend.Exception.ChatException;
import com.example.backend.Exception.MessageException;
import com.example.backend.Exception.UserException;
import com.example.backend.Repository.MessageRepo;
import com.example.backend.model.Chat;
import com.example.backend.model.Message;
import com.example.backend.model.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private MessageRepo messageRepo;

    private UserService userService;

    private ChatService chatService;

    private SimpMessagingTemplate simpMessagingTemplate;

    public MessageService(MessageRepo messageRepo, UserService userService, ChatService chatService, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageRepo = messageRepo;
        this.userService = userService;
        this.chatService = chatService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public Message sendMessage(SendMessageRequest sendMessageRequest) {

        User user = userService.findUserById(sendMessageRequest.getUserid());
        Chat chat = chatService.findChatById(sendMessageRequest.getChatid());

        Message message = new Message();
        message.setUser(user);
        message.setChat(chat);
        message.setMessage(sendMessageRequest.getMessage());

        message = messageRepo.save(message);

        if (chat.isGroupChat()) {

            simpMessagingTemplate.convertAndSend(
                    "/topic/group/" + chat.getId(),
                    sendMessageRequest.getMessage()   // ✅ only string
            );

        }
        // Single Chat
        else {

            UUID receiverUserId = chat.getUsers()
                    .stream()
                    .filter(u -> !u.getId().equals(user.getId()))
                    .findFirst()
                    .orElseThrow(() -> new UserException("Receiver not found"))
                    .getId();

            simpMessagingTemplate.convertAndSendToUser(
                    receiverUserId.toString(),
                    "/queue/messages",
                    sendMessageRequest.getMessage()   // ✅ only string
            );

            simpMessagingTemplate.convertAndSendToUser(
                    user.getId().toString(),
                    "/queue/messages",
                    sendMessageRequest.getMessage()   // ✅ only string
            );
        }

        return message;
    }


    public List<Message> getChatsMessages(UUID chatId, User reqUser) throws UserException, ChatException {
        Chat chat = this.chatService.findChatById(chatId);

        if (!chat.getUsers().contains(reqUser)) {
            throw new UserException("You are not related to this chat");
        }

        List<Message> messages = messageRepo.findByChatId(chat.getId());

        return messages;
    }

    public void deleteMessage(UUID messageId,User reqUser) throws MessageException {
        Message message = messageRepo.findById(messageId)
                .orElseThrow(() -> new MessageException("The required message is not found"));

        if (message.getUser().getId() == reqUser.getId()) {
            messageRepo.delete(message);
        } else {
            throw new MessageException("You are not authorized for this task");
        }
    }

}
