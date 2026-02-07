package com.example.backend.controller;

import com.example.backend.Dto.ApiResponse;
import com.example.backend.Dto.MessageResponseDto;
import com.example.backend.Dto.SendMessageRequest;
import com.example.backend.Exception.ChatException;
import com.example.backend.Exception.MessageException;
import com.example.backend.Exception.UserException;
import com.example.backend.model.Chat;
import com.example.backend.model.Message;
import com.example.backend.model.User;
import com.example.backend.service.ChatService;
import com.example.backend.service.MessageService;
import com.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.server.UID;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@Tag(
        name = "Message Control",
        description = "APIs for sending, retrieving, and deleting messages in chats. These endpoints support real-time chat communication and require JWT authentication."
)
public class MessageController {

        private MessageService messageService;
        private UserService userService;

        private MessageController(MessageService messageService, UserService userService) {
            this.messageService = messageService;
            this.userService = userService;
        }

        @PostMapping("/create")
        @Operation(summary = "Send a new message", description = "Allows an authenticated user to send a message to a specific chat (single or group).\n" +
                "The message will be stored in the database and can be delivered in real-time using WebSocket if implemented.")
        public ResponseEntity<MessageResponseDto> sendMessageHandler(
                @RequestBody SendMessageRequest sendMessageRequest,
                @RequestHeader("Authorization") String jwt
        ) throws UserException, ChatException {

                User user = userService.FindUser(jwt);

                sendMessageRequest.setUserid(user.getId());

                Message message = messageService.sendMessage(sendMessageRequest,user);

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

                return new ResponseEntity<>(dto, HttpStatus.OK);
        }


        @GetMapping("/{chatId}")
        @Operation(summary = "Get all messages of a chat", description = "Fetches the complete message history of a particular chat using the chatId.\n" +
                "Only users who are members of the chat are allowed to view the messages.")
        public ResponseEntity<List<MessageResponseDto>> getChatMessageHandler(
                @PathVariable UUID chatId,
                @RequestHeader("Authorization") String jwt
        ) throws UserException, ChatException {

                User user = userService.FindUser(jwt);

                List<Message> messages = messageService.getChatsMessages(chatId, user);

                List<MessageResponseDto> messageDtos = messages.stream().map(message -> {

                        MessageResponseDto dto = new MessageResponseDto();
                        dto.setId(message.getId());
                        dto.setMessage(message.getMessage());
                        dto.setCreatedAt(message.getCreatedAt());
                        dto.setChatId(chatId);

                        if (message.getUser() != null) {
                                dto.setSenderId(message.getUser().getId());
                                dto.setSenderName(message.getUser().getName());
                        }

                        return dto;

                }).toList();

                return new ResponseEntity<>(messageDtos, HttpStatus.OK);
        }


        @DeleteMapping("/{messageId}")
        @Operation(summary = "Delete a message",description = "Deletes a specific message using messageId.\n" +
                "Only the sender of the message (or admin if implemented) is authorized to delete it.\n" +
                "After deletion, the message will be removed permanently (or marked as deleted depending on implementation)")
        public ResponseEntity<ApiResponse> deleteMessageHandler(@PathVariable UUID messageId,
                                                                @RequestHeader("Authorization") String jwt) throws UserException, MessageException {

                User user = userService.FindUser(jwt);

                messageService.deleteMessage(messageId, user);

                ApiResponse res = new ApiResponse("Deleted successfully......", false);

                return new ResponseEntity<>(res, HttpStatus.OK);
        }

}
