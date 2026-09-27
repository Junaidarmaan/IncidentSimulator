package com.junnu.incidentsim.ai;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.junnu.incidentsim.ai.assistants.Assistant;
import com.junnu.incidentsim.ai.assistants.IncidentAssistant;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;

@Configuration
public class AIConfig {
    @Bean
    GoogleAiGeminiChatModel initializeGeminiChatModel() {

        GoogleAiGeminiChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                // .modelName("gemini-3.5-flash-lite")
                .modelName("gemini-3.1-flash-lite")

                // .modelName("gemma-4-31b-it")
                // .modelName("gemma-4-26b-it")

                .sendThinking(true)
                .returnThinking(true)
                .build();

        return model;
    }

    @Bean
    IncidentAssistant initializeAssistant(GoogleAiGeminiChatModel model, Tools tools) {
        MessageWindowChatMemory memory = MessageWindowChatMemory.withMaxMessages(20);
        return AiServices.builder(IncidentAssistant.class)
                .chatModel(model)
                .chatMemory(memory)
                .tools(tools)
                .build();
    }
     @Bean
    Assistant initializeChatAssistant(GoogleAiGeminiChatModel model, Tools tools) {
        MessageWindowChatMemory memory = MessageWindowChatMemory.withMaxMessages(20);
        return AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(memory)
                .tools(tools)
                .build();
    }
}
