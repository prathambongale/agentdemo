package com.cursor.ai.agentdemo.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Ollama integration
 */
@Configuration
public class OllamaConfig {

    private static final String OLLAMA_BASE_URL = "http://localhost:11434";
    
    @Bean
    public ChatModel baseChatModel() {
        // Using llama3.1 for general tasks
        return OllamaChatModel.builder()
                .baseUrl(OLLAMA_BASE_URL)
                .modelName("llama3.1:latest")
                .temperature(0.3)
                .build();
    }
    
    @Bean
    public ChatModel plannerChatModel() {
        // Using deepseek-r1 for planning and decision-making tasks
        return OllamaChatModel.builder()
                .baseUrl(OLLAMA_BASE_URL)
                .modelName("deepseek-r1:latest")
                .temperature(0.2)
                .build();
    }
}

