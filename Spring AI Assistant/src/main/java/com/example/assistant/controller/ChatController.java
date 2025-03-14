package com.example.assistant.controller;

import com.example.assistant.payload.CricketResponse;
import com.example.assistant.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api/v1/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;
    @GetMapping
    public ResponseEntity<String>generateResponse(
            @RequestParam(value = "inputText")String inputText
    ){
   return ResponseEntity.ok(chatService.generateResponse(inputText));
    }

    @GetMapping("/stream")
    public Flux<String> streamResponse(
            @RequestParam(value = "inputText")String inputText
    ){
        return chatService.streamResponse(inputText);
    }

    @GetMapping("/cricket-bot")
    public ResponseEntity<CricketResponse> getCricketResponse(
            @RequestParam(value = "inputText")String inputText
    ) {
        try {
            return ResponseEntity.ok(chatService.generateCricketResponse(inputText));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/images")
    public ResponseEntity<List<String>> generateImage(
            @RequestParam("imageDescription")String imageDescription,
            @RequestParam(value = "size",required = false,defaultValue = "512x512")String size,
            @RequestParam(value = "numberOfImages",required = false,defaultValue = "2")int numbers
    ){
        try {
            return ResponseEntity.ok(chatService.generateImages(imageDescription,size,numbers));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
