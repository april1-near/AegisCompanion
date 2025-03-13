package com.smartcommunity.smart_community_platform.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private final OpenAiChatModel chatModel;


    @MessageMapping("/test")
    public String handleMessageUser(String message) {
        System.out.println("收到消息" + message);
        String destination = "/queue/messages";

        messagingTemplate.convertAndSendToUser("TestUser",
                destination, "Server回应:" + message
        );

        return "Server回应: " + message;
    }


    @MessageMapping("/chat.stream")
    public void handleStreamingChat(
            String message,
            Principal principal) {

        String userId = principal.getName(); // 或 principal.getName()
        String destination = "/queue/ai-stream";

        log.info("收到用户 {} 的流式请求: {}", userId, message);

        Prompt prompt = new Prompt(new UserMessage(message));
        Flux<ChatResponse> responseFlux = chatModel.stream(prompt);
        responseFlux
                .map(chatResponse -> {
                    // 提取有效文本内容
                    AssistantMessage output = chatResponse.getResult().getOutput();
                    return output.getText();
                })
                .filter(content -> !"[BLANK]".equals(content)) // 过滤空内容
                .doOnNext(content -> {
                    messagingTemplate.convertAndSendToUser(
                            userId,
                            destination,
                            content
                    );
                })
                .doOnComplete(() -> {
                    // 发送结束标志
                    messagingTemplate.convertAndSendToUser(
                            userId,
                            destination,
                            "[BLANK]"
                    );
                })
                .subscribe();
    }
}
