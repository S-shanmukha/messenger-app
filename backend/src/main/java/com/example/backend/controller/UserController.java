package com.example.backend.controller;

import com.example.backend.Dto.UserResponseDto;
import com.example.backend.Exception.UserException;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getUserProfileHandler(
            @RequestHeader("Authorization") String jwt
    ) throws UserException {

        User user = userService.FindUser(jwt);

        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}
