package com.smartcommunity.smart_community_platform.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    ZhiPuAiApi zhipuAiApiKey = new ZhiPuAiApi(System.getenv("CHAT_API_KEY"));

    @Bean
    ZhiPuAiChatModel zhiPuAiChatModel() {
        return
                new ZhiPuAiChatModel(zhipuAiApiKey, ZhiPuAiChatOptions.builder()
                        .model("glm-4-plus")
                        .temperature(0.4)
                        .maxTokens(200)
                        .build());

    }

    @Bean
    ChatClient chatClient(ZhiPuAiChatModel zhiPuAiChatModel, ChatMemory chatMemory) {
        return ChatClient.builder(zhiPuAiChatModel).
                defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

}
