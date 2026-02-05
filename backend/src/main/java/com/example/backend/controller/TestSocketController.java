package com.example.backend.controller;

//package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestSocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/test-topic")
    public String testTopic() {
        simpMessagingTemplate.convertAndSend("/topic/test", "Hello from backend REST!");
        return "Sent message to /topic/test";
    }
}

