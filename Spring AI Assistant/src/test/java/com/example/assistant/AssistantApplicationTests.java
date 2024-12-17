package com.example.assistant;

import com.example.assistant.payload.CricketResponse;
import com.example.assistant.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AssistantApplicationTests {


    @Autowired
    private ChatService chatService;

    @Test
    void contextLoads() throws JsonProcessingException {
		CricketResponse cricketResponse = chatService.generateCricketResponse("Who is Sachin ?");
        System.out.println(cricketResponse);
	}

}
