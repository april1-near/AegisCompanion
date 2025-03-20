package com.aegis.companion.chatMemory;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component

public class RedisChatMemory implements ChatMemory {

    private static final String KEY_PREFIX = "chat:memory:";
    private final RedisTemplate<String, Message> redisTemplate;

    public RedisChatMemory(RedisTemplate<String, Message> redisTemplate) {
        this.redisTemplate = Objects.requireNonNull(redisTemplate, "RedisTemplate 不能为空");
    }

    @Override
    public void add(String conversationId, Message message) {
        validateConversationId(conversationId);
        String key = buildRedisKey(conversationId);
        // 使用右追加保证消息顺序
        redisTemplate.opsForList().rightPush(key, message);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        validateConversationId(conversationId);
        if (messages == null || messages.isEmpty()) return;

        String key = buildRedisKey(conversationId);
        // 批量写入优化
        redisTemplate.opsForList().rightPushAll(key, messages);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        validateConversationId(conversationId);

        String key = buildRedisKey(conversationId);
        Long totalMessages = redisTemplate.opsForList().size(key);

        if (totalMessages == null || totalMessages == 0) {
            return Collections.emptyList();
        }

        // 处理请求数量超过实际消息数的情况
        int endIndex = -1; // Redis中-1表示最后一个元素
        int startIndex = (lastN >= totalMessages) ? 0 : (int) (-Math.min(lastN, totalMessages));

        return redisTemplate.opsForList().range(key, startIndex, endIndex);
    }

    @Override
    public void clear(String conversationId) {
        validateConversationId(conversationId);
        redisTemplate.delete(buildRedisKey(conversationId));
    }

    // 辅助方法
    private String buildRedisKey(String conversationId) {
        return KEY_PREFIX + conversationId;
    }

    private void validateConversationId(String conversationId) {
        if (conversationId == null || conversationId.trim().isEmpty()) {
            throw new IllegalArgumentException("会话ID不能为空");
        }
    }
}
