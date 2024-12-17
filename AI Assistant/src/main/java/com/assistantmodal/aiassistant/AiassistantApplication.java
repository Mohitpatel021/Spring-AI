package com.assistantmodal.aiassistant;

import com.assistantmodal.aiassistant.gui.MainApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class AiassistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiassistantApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void handleAppStart(){
		MainApplication.launchApplication();
	}

}
