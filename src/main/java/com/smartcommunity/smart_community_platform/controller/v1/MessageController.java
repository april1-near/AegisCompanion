package com.smartcommunity.smart_community_platform.controller.v1;

import com.smartcommunity.smart_community_platform.service.MessageTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessagingTemplate messagingTemplate;

    // 处理客户端发送到/app/hello的消息
    @MessageMapping("/hello")
    @SendTo({"/topic/greetings"}) // 将返回值广播到/topic/greetings
    public String handleMessage(String message) {
        System.out.println("收到消息" + message);
        return "Server回应: " + message;
    }

    @MessageMapping("/test")
    public String handleMessageUser(String message) {
        System.out.println("收到消息" + message);
        String destination = "/queue/messages";

        messagingTemplate.convertAndSendToUser("TestUser",
                destination, "Server回应:" + message
        );
        return "Server回应: " + message;
    }


}
