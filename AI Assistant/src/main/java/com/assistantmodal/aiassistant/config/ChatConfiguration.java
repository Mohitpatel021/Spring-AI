package com.assistantmodal.aiassistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfiguration {
    @Bean
    public ChatClient chactClient(ChatClient.Builder chatClientBuilder){
        return chatClientBuilder.build();
    }
}
