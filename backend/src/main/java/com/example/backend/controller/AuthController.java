package com.example.backend.controller;

import com.example.backend.Dto.AuthResponse;
import com.example.backend.Dto.LoginRequest;
import com.example.backend.Dto.SignUpRequest;
import com.example.backend.Exception.UserException;
import com.example.backend.config.TokenProvider;
import com.example.backend.config.UserSecurityService;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name="Authentication Controller", description = "This module handles user authentication and authorization.\n" +
        "It provides APIs for user registration, login, and JWT-based authentication to securely access protected resources.")
public class AuthController {

             private UserService userService;
             private TokenProvider tokenProvider;
             private PasswordEncoder passwordEncoder;
             private UserSecurityService userSecurityService;

             public  AuthController(UserService userService, TokenProvider tokenProvider, PasswordEncoder passwordEncoder, UserSecurityService userSecurityService) {
                 this.userService = userService;
                     this.tokenProvider = tokenProvider;
                     this.passwordEncoder = passwordEncoder;
                     this.userSecurityService = userSecurityService;
             }



            @PostMapping("/signup")
            @Operation(summary = "Register a new user",description = "Creates a new user account by collecting required details such as name, email, and password.\n" +
                    "If the email already exists, registration will be rejected.")
            public ResponseEntity<AuthResponse> UserSignUp(@RequestBody SignUpRequest signUpRequest) throws UserException{
                String email = signUpRequest.getEmail();
                String password = signUpRequest.getPassword();
                String name = signUpRequest.getName();
                boolean userAlreadyExist=userService.UserAlreadyExist(email);
                if(userAlreadyExist){
                    throw new UserException("User with this email already exist");
                }
                String encodedPassword = passwordEncoder.encode(password);
                User newUser = new User(name, email, encodedPassword);
                userService.addUser(newUser);
                Authentication authentication = authenticate(email, password);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                String jwt = tokenProvider.generateToken(authentication);

                AuthResponse authResponse=new AuthResponse(jwt,true);
                return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.ACCEPTED);

            }

            @PostMapping("/signin")
            @Operation(summary = "Authenticate user and generate token",description = "Validates user credentials (email + password).\n" +
                    "If authentication is successful, the server generates a JWT token and returns it.\n" +
                    "This token must be used in future requests for accessing secured APIs.")
            public ResponseEntity<AuthResponse> UserSignIn(@RequestBody LoginRequest loginRequest) throws UserException{
                        String  email = loginRequest.getEmail();
                        String password = loginRequest.getPassword();
                        Authentication authentication = authenticate(email, password);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        String jwt = tokenProvider.generateToken(authentication);
                        AuthResponse authResponse=new AuthResponse(jwt,true);

                        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.ACCEPTED);

            }

            public Authentication authenticate(String Email, String encodedPassword) {
                UserDetails userDetails= userSecurityService.loadUserByUsername(Email);

                if(userDetails == null) {
                    throw new BadCredentialsException("Invalid email or password");
                }
                if(!passwordEncoder.matches(encodedPassword, userDetails.getPassword())) {
                    throw new BadCredentialsException("Invalid email or password");
                }

                return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
            }
}
