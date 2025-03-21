package com.aegis.companion.config;

import com.aegis.companion.chatMemory.RedisChatMemory;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@RequiredArgsConstructor
public class ChatConfig {

    private final RedisTemplate<String, Message> redisTemplate;
    ZhiPuAiApi zhipuAiApiKey = new ZhiPuAiApi(System.getenv("CHAT_API_KEY"));

    @Bean
    ChatMemory chatMemory() {
        return new RedisChatMemory(redisTemplate);

    }

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
