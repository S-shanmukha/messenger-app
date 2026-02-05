package com.example.backend.service;

import com.example.backend.Dto.GroupChatRequest;
import com.example.backend.Exception.ChatException;
import com.example.backend.Exception.UserException;
import com.example.backend.Repository.ChatRepo;
import com.example.backend.Repository.UserRepo;
import com.example.backend.model.Chat;
import com.example.backend.model.User;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

        private ChatRepo chatRepo;
        private UserRepo userRepo;
        private UserService userService;
        public ChatService(ChatRepo chatRepo, UserRepo userRepo, UserService userService) {
            this.chatRepo = chatRepo;
            this.userRepo = userRepo;
            this.userService = userService;
        }

        public Chat CreateSingleChat(User user1, UUID userid) throws UserException {
            User user2=userRepo.findById(userid)
                    .orElseThrow(() -> new UserException("User not found"));

            Chat ExistingChat=chatRepo.findSingleChatByUserIds(user2,user1);

            if(ExistingChat!=null){
                return ExistingChat;
            }
            Chat newChat=new Chat();
            newChat.setGroupChat(false);
            newChat.getUsers().add(user1);
            newChat.getUsers().add(user2);
            newChat.setCreatedBy(user1);

            chatRepo.save(newChat);

            return newChat;

        }

    public Chat findChatById(UUID chatId) throws ChatException {

        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new ChatException("requested Chat not found"));

        return chat;
    }


    public Chat CreateGroup(GroupChatRequest req, User requser) throws UserException {
            Chat group = new Chat();
            group.setGroupChat(true);
            group.setChatName(req.getChatName());
            group.setCreatedBy(requser);
            group.getAdmins().add(requser);
            group.getUsers().add(requser);

            for (UUID userId : req.getUserids()) {
                User user = userService.findUserById(userId);
                group.getUsers().add(user);
            }

            group = chatRepo.save(group);
            return group;
        }

        public List<Chat> findAllChatByUserId(UUID userId) throws UserException {
        User user = userService.findUserById(userId);

        List<Chat> chats = chatRepo.findChatByUserId(user.getId());

        return chats;
        }

    public Chat addUserToGroup(UUID userId, UUID chatId, User reqUser) throws UserException, ChatException {
        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new ChatException("The expected chat is not found"));
        System.out.println(chat);

        User user = userRepo.findById(userId).orElseThrow(() -> new UserException("The expected chat is not found"));;

        if (chat.getAdmins().contains((reqUser))) {
            chat.getUsers().add(user);
            return chatRepo.save(chat);
        } else {
            throw new UserException("You have not access to add user");
        }
    }

    public Chat removeFromGroup(UUID userId, UUID chatId, User reqUser) throws UserException, ChatException {
        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new ChatException("The expected chat is not found"));

        User user = userService.findUserById(userId);
        if(!chat.isGroupChat()){
            throw new ChatException("Cant perform remove from single chat");
        }
        if (chat.getAdmins().contains((reqUser))) {
            chat.getUsers().remove(user);
            return chatRepo.save(chat);
        } else if (chat.getUsers().contains(reqUser)) {
            if (user.getId() == reqUser.getId()) {
                chat.getUsers().remove(user);
                return chatRepo.save(chat);
            }

        }
        throw new UserException("You have not access to remove user");

    }

    public void deleteChat(UUID chatId, UUID userId) throws ChatException, UserException {
        Chat chat = this.chatRepo.findById(chatId)
                .orElseThrow(() -> new ChatException("The expected chat is not found while deleteing"));
        chatRepo.delete(chat);
    }

}
