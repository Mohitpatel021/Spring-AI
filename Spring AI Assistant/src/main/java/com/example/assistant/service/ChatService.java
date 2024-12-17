package com.example.assistant.service;

import com.example.assistant.payload.CricketResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.*;

import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired
    private ChatModel chatModel;

    @Autowired
    private StreamingChatModel streamingChatModel;

    @Autowired
    private ImageModel imageModel;
    public String generateResponse(String inputText) {
        return chatModel.call(inputText);
    }
    public Flux<String> streamResponse(String inputText) {
        return chatModel.stream(inputText);
    }

    public CricketResponse generateCricketResponse(String inputText) throws JsonProcessingException {
        String promptString ="Hello";

        ChatResponse cricketResponse = chatModel.call(
                new Prompt(promptString)
        );
        String responseString = cricketResponse.getResult().getOutput().getContent();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseString, CricketResponse.class);
    }

    public String loadPromptTemplate(String fileName) throws IOException {
        Path path = new ClassPathResource(fileName).getFile().toPath();
        return Files.readString(path);
    }

    public String putValuesInPromptsTempalte(String template, Map<String,String> values){
        for(Map.Entry<String,String>entry:values.entrySet()){
            template=template.replace("{"+entry.getKey()+"}",entry.getValue());
        }
        return template;
    }
    public List<String> generateImages(String imageDesc, String size, int number) throws IOException {

        String promptString = this.putValuesInPromptsTempalte(this.loadPromptTemplate("prompts/image_bot.txt"), Map.of(
                "description", imageDesc
        ));
        ImageResponse imageResponse = imageModel.call(new ImagePrompt(promptString, OpenAiImageOptions.builder()
                .withModel("dall-e-3")
                .withN(number)
                .withHeight(1024)
                .withWidth(1024)
                .withQuality("standard")
                .build()));
        return imageResponse.getResults().stream().map(generation -> generation.getOutput().getUrl()).toList();
    }



}
