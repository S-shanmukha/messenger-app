package com.example.backend.controller;

import com.example.backend.Dto.*;
import com.example.backend.Exception.ChatException;
import com.example.backend.Exception.UserException;
import com.example.backend.model.Chat;
import com.example.backend.model.Message;
import com.example.backend.model.User;
import com.example.backend.service.ChatService;
import com.example.backend.service.MessageService;
import com.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats")
@Tag(name = "Chat Controller",description="Handles chat creation, group management, and chat retrieval operations")
public class ChatController {

    private UserService userService;

    private ChatService chatService;

    public ChatController(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }

    @PostMapping("/single")
    @Operation(summary = "Create a single (one-to-one) chat",description = "Creates a new private chat between two users.\n" +
            "If a chat already exists between the same users, the existing chat will be returned instead of creating a duplicate.\n" +
            "Only authenticated users can create chats.")
    public ResponseEntity<ChatResponseDto> createChat(
            @RequestBody SingleChatRequest singleChatRequest,
            @RequestHeader("Authorization") String jwt
    ) throws UserException {

        User reqUser = userService.FindUser(jwt);

        Chat chat = chatService.CreateSingleChat(reqUser, singleChatRequest.getUserid());

        ChatResponseDto chatResponseDto = new ChatResponseDto();
        chatResponseDto.setId(chat.getId());
        chatResponseDto.setGroupChat(chat.isGroupChat());
        chatResponseDto.setChatName(chat.getChatName());

        // convert users list
        List<UserResponseDto> userDtos = chat.getUsers().stream().map(user -> {
            UserResponseDto dto = new UserResponseDto();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            return dto;
        }).toList();

        chatResponseDto.setUsers(userDtos);

        // convert admins list
        List<UserResponseDto> adminDtos = chat.getAdmins().stream().map(user -> {
            UserResponseDto dto = new UserResponseDto();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            return dto;
        }).toList();

        chatResponseDto.setAdmins(adminDtos);

        MessageResponseDto lastMessageDto = null;

        if (chat.getMessages() != null && !chat.getMessages().isEmpty()) {
            Message lastMessage = chat.getMessages().get(chat.getMessages().size() - 1);

            lastMessageDto = new MessageResponseDto();
            lastMessageDto.setId(lastMessage.getId());
            lastMessageDto.setMessage(lastMessage.getMessage());
            lastMessageDto.setCreatedAt(lastMessage.getCreatedAt());

            if (lastMessage.getUser() != null) {
                lastMessageDto.setSenderId(lastMessage.getUser().getId());
                lastMessageDto.setSenderName(lastMessage.getUser().getName());
            }
        }

        chatResponseDto.setLastMessage(lastMessageDto);

        chatResponseDto.setCreatedAt(chat.getCreatedAt());

        return new ResponseEntity<>(chatResponseDto, HttpStatus.OK);
    }


    @PostMapping("/group")
    @Operation(summary = "Create a group chat", description = "Creates a new group chat with multiple participants.\n" +
            "The authenticated user becomes the group creator/admin by default.\n" +
            "Group details such as group name and members are stored in the database.")
    public ResponseEntity<ChatResponseDto> createGroupHandler(
            @RequestBody GroupChatRequest groupChatRequest,
            @RequestHeader("Authorization") String jwt
    ) throws UserException {

        User reqUser = userService.FindUser(jwt);

        Chat chat = chatService.CreateGroup(groupChatRequest, reqUser);

        ChatResponseDto chatResponseDto = new ChatResponseDto();
        chatResponseDto.setId(chat.getId());
        chatResponseDto.setGroupChat(chat.isGroupChat());
        chatResponseDto.setChatName(chat.getChatName());

        // convert users list
        List<UserResponseDto> userDtos = chat.getUsers().stream().map(user -> {
            UserResponseDto dto = new UserResponseDto();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            return dto;
        }).toList();

        chatResponseDto.setUsers(userDtos);

        // convert admins list
        List<UserResponseDto> adminDtos = chat.getAdmins().stream().map(user -> {
            UserResponseDto dto = new UserResponseDto();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            return dto;
        }).toList();

        chatResponseDto.setAdmins(adminDtos);

        MessageResponseDto lastMessageDto = null;

        if (chat.getMessages() != null && !chat.getMessages().isEmpty()) {
            Message lastMessage = chat.getMessages().get(chat.getMessages().size() - 1);

            lastMessageDto = new MessageResponseDto();
            lastMessageDto.setId(lastMessage.getId());
            lastMessageDto.setMessage(lastMessage.getMessage());
            lastMessageDto.setCreatedAt(lastMessage.getCreatedAt());

            if (lastMessage.getUser() != null) {
                lastMessageDto.setSenderId(lastMessage.getUser().getId());
                lastMessageDto.setSenderName(lastMessage.getUser().getName());
            }
        }

        chatResponseDto.setLastMessage(lastMessageDto);

        chatResponseDto.setCreatedAt(chat.getCreatedAt());


        return new ResponseEntity<>(chatResponseDto, HttpStatus.CREATED);
    }


    @GetMapping("/{chatId}")
    @Operation(summary = "Get chat details by chat ID",description = "Fetches complete chat information including chat type (single/group), members list, and chat metadata.\n" +
            "Only users who are part of the chat are allowed to access this endpoint.")
    public ResponseEntity<ChatDetailsResponseDto> findChatByIdHandler(
            @PathVariable UUID chatId
    ) throws ChatException {

        Chat chat = chatService.findChatById(chatId);

        ChatDetailsResponseDto dto = new ChatDetailsResponseDto();
        dto.setId(chat.getId());
        dto.setGroupChat(chat.isGroupChat());
        dto.setChatName(chat.getChatName());

        // Users
        List<UserResponseDto> userDtos = chat.getUsers().stream().map(user -> {
            UserResponseDto userDto = new UserResponseDto();
            userDto.setId(user.getId());
            userDto.setName(user.getName());
            userDto.setEmail(user.getEmail());
            return userDto;
        }).toList();
        dto.setUsers(userDtos);

        // Admins
        List<UserResponseDto> adminDtos = chat.getAdmins().stream().map(user -> {
            UserResponseDto adminDto = new UserResponseDto();
            adminDto.setId(user.getId());
            adminDto.setName(user.getName());
            adminDto.setEmail(user.getEmail());
            return adminDto;
        }).toList();
        dto.setAdmins(adminDtos);

        // Messages
        List<MessageResponseDto> messageDtos = chat.getMessages().stream().map(message -> {
            MessageResponseDto msgDto = new MessageResponseDto();
            msgDto.setId(message.getId());
            msgDto.setMessage(message.getMessage());
            msgDto.setCreatedAt(message.getCreatedAt());

            if (message.getUser() != null) {
                msgDto.setSenderId(message.getUser().getId());
                msgDto.setSenderName(message.getUser().getName());
            }

            return msgDto;
        }).toList();

        dto.setMessages(messageDtos);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @GetMapping("/user")
    @Operation(summary = "Get all chats of logged-in user", description = "Returns the list of all chats (single and group) in which the authenticated user is a member.\n" +
            "Each chat contains basic details like members, chat name, and last message info.")
    public ResponseEntity<List<ChatListResponseDto>> findChatByUserIdHandler(
            @RequestHeader("Authorization") String jwt
    ) throws UserException {

        User reqUser = userService.FindUser(jwt);

        List<Chat> chats = chatService.findAllChatByUserId(reqUser.getId());

        List<ChatListResponseDto> chatDtos = chats.stream().map(chat -> {

            ChatListResponseDto dto = new ChatListResponseDto();
            dto.setId(chat.getId());
            dto.setGroupChat(chat.isGroupChat());
            dto.setChatName(chat.getChatName());

            // last message
            MessageResponseDto lastMessageDto = null;
            if (chat.getMessages() != null && !chat.getMessages().isEmpty()) {

                Message lastMessage = chat.getMessages().get(chat.getMessages().size() - 1);

                lastMessageDto = new MessageResponseDto();
                lastMessageDto.setId(lastMessage.getId());
                lastMessageDto.setMessage(lastMessage.getMessage());
                lastMessageDto.setCreatedAt(lastMessage.getCreatedAt());

                if (lastMessage.getUser() != null) {
                    lastMessageDto.setSenderId(lastMessage.getUser().getId());
                    lastMessageDto.setSenderName(lastMessage.getUser().getName());
                }
            }

            dto.setLastMessage(lastMessageDto);

            return dto;

        }).toList();

        return new ResponseEntity<>(chatDtos, HttpStatus.OK);
    }


    @PutMapping("/{chatId}/add/{userId}")
    @Operation(summary = "Add user to group chat", description = "Adds a new user into an existing group chat.\n" +
            "Only the group admin/authorized members can add new participants.\n" +
            "If the user is already in the group, the request will be rejected.")
    public ResponseEntity<ChatResponseDto> addUserToGroupHandler(
            @PathVariable UUID chatId,
            @PathVariable UUID userId,
            @RequestHeader("Authorization") String jwt
    ) throws UserException, ChatException {

        User reqUser = userService.FindUser(jwt);

        Chat chat = chatService.addUserToGroup(userId, chatId, reqUser);

        ChatResponseDto dto = new ChatResponseDto();
        dto.setId(chat.getId());
        dto.setGroupChat(chat.isGroupChat());
        dto.setChatName(chat.getChatName());

        // users
        List<UserResponseDto> users = chat.getUsers().stream().map(user -> {
            UserResponseDto u = new UserResponseDto();
            u.setId(user.getId());
            u.setName(user.getName());
            u.setEmail(user.getEmail());
            return u;
        }).toList();
        dto.setUsers(users);

        // admins
        List<UserResponseDto> admins = chat.getAdmins().stream().map(user -> {
            UserResponseDto u = new UserResponseDto();
            u.setId(user.getId());
            u.setName(user.getName());
            u.setEmail(user.getEmail());
            return u;
        }).toList();
        dto.setAdmins(admins);

        // lastMessage
        MessageResponseDto lastMessageDto = null;
        if (chat.getMessages() != null && !chat.getMessages().isEmpty()) {
            Message lastMessage = chat.getMessages().get(chat.getMessages().size() - 1);

            lastMessageDto = new MessageResponseDto();
            lastMessageDto.setId(lastMessage.getId());
            lastMessageDto.setMessage(lastMessage.getMessage());
            lastMessageDto.setCreatedAt(lastMessage.getCreatedAt());

            if (lastMessage.getUser() != null) {
                lastMessageDto.setSenderId(lastMessage.getUser().getId());
                lastMessageDto.setSenderName(lastMessage.getUser().getName());
            }
        }
        dto.setLastMessage(lastMessageDto);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @PutMapping("/{chatId}/remove/{userId}")
    @Operation(summary = "Remove user from group chat",description = "Removes a user from an existing group chat.\n" +
            "Only admin/authorized users can remove members.\n" +
            "If the user is not part of the group, an error will be returned.")
    public ResponseEntity<ChatResponseDto> removeUserFromGroupHandler(
            @PathVariable UUID chatId,
            @PathVariable UUID userId,
            @RequestHeader("Authorization") String jwt
    ) throws UserException, ChatException {

        User reqUser = userService.FindUser(jwt);

        Chat chat = chatService.removeFromGroup(userId,chatId,reqUser);

        ChatResponseDto dto = new ChatResponseDto();
        dto.setId(chat.getId());
        dto.setGroupChat(chat.isGroupChat());
        dto.setChatName(chat.getChatName());

        // users
        List<UserResponseDto> users = chat.getUsers().stream().map(user -> {
            UserResponseDto u = new UserResponseDto();
            u.setId(user.getId());
            u.setName(user.getName());
            u.setEmail(user.getEmail());
            return u;
        }).toList();
        dto.setUsers(users);

        // admins
        List<UserResponseDto> admins = chat.getAdmins().stream().map(user -> {
            UserResponseDto u = new UserResponseDto();
            u.setId(user.getId());
            u.setName(user.getName());
            u.setEmail(user.getEmail());
            return u;
        }).toList();
        dto.setAdmins(admins);

        // lastMessage
        MessageResponseDto lastMessageDto = null;
        if (chat.getMessages() != null && !chat.getMessages().isEmpty()) {
            Message lastMessage = chat.getMessages().get(chat.getMessages().size() - 1);

            lastMessageDto = new MessageResponseDto();
            lastMessageDto.setId(lastMessage.getId());
            lastMessageDto.setMessage(lastMessage.getMessage());
            lastMessageDto.setCreatedAt(lastMessage.getCreatedAt());

            if (lastMessage.getUser() != null) {
                lastMessageDto.setSenderId(lastMessage.getUser().getId());
                lastMessageDto.setSenderName(lastMessage.getUser().getName());
            }
        }
        dto.setLastMessage(lastMessageDto);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @DeleteMapping("/delete/{chatId}")
    @Operation(summary = "Delete a chat",description = "Deletes a chat permanently along with its related messages (or marks it inactive depending on implementation).\n" +
            "Only the chat creator/admin is allowed to delete the chat.\n" +
            "This action cannot be undone.")
    public ResponseEntity<ApiResponse> deleteChatHandler(@PathVariable UUID chatId,
                                                         @RequestHeader("Authorization") String jwt)
            throws UserException, ChatException {

        User reqUser = userService.FindUser(jwt);

        this.chatService.deleteChat(chatId, reqUser.getId());

        ApiResponse res = new ApiResponse("Deleted Successfully...", false);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
