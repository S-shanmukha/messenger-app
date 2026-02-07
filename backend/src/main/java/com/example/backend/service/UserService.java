package com.example.backend.service;

import com.example.backend.Exception.UserException;
import com.example.backend.Repository.UserRepo;
import com.example.backend.config.TokenProvider;
import com.example.backend.model.User;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private UserRepo  userRepo;
    private TokenProvider tokenProvider;
    public UserService(UserRepo userRepo, TokenProvider tokenProvider) {
        this.userRepo = userRepo;
        this.tokenProvider = tokenProvider;
    }
    public User finduserByemail(String email) throws UserException{
//        String email=tokenProvider.getEmailFromToken(jwt);
        User user=userRepo.findByEmail(email);
        if(user==null) {
            throw new UserException("User not found");
        }
        return user;
    }

    public boolean UserAlreadyExist(String email) {
            User user=userRepo.findByEmail(email);
            if(user==null) {
                return false;
            }
            return true;
    }

    public void addUser(User user) {
        userRepo.save(user);
    }

    public User findUserById(UUID id) throws UserException {
        return this.userRepo.findById(id).orElseThrow(() -> new UserException("The requested user is not found"));
    }

    public User FindUser(String jwt) throws UserException {
            String email=tokenProvider.getEmailFromToken(jwt);
            User user=userRepo.findByEmail(email);
            if(user==null) {
                throw new UserException("User not found");
            }
            return user;
    }



}
