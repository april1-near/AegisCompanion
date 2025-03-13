package com.smartcommunity.smart_community_platform.config;

// file: config/WebSocketConfig.java

import com.smartcommunity.smart_community_platform.security.WebSocketAuthInterceptor;
import com.smartcommunity.smart_community_platform.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket配置类
 * 1. 启用STOMP协议支持
 * 2. 配置消息代理和端点
 */

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;


    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用简单内存消息代理（生产环境建议使用RabbitMQ作为外部代理）
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setUserDestinationPrefix("/user");
        // 设置应用程序目标前缀
        registry.setApplicationDestinationPrefixes("/app");
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("🛠️ 注册 WebSocket 端点: /ws");

        registry.addEndpoint("/ws")
                .setHandshakeHandler(webSocketAuthInterceptor())
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(5000); // 设置心跳间隔

        registry.addEndpoint("/ws-native") // 新增原生WebSocket端点
                .setHandshakeHandler(webSocketAuthInterceptor())
                .setAllowedOriginPatterns("*");

    }

    @Bean
    public WebSocketAuthInterceptor webSocketAuthInterceptor() {
        return new WebSocketAuthInterceptor(jwtUtil);
    }


    @Bean
    public ChannelInterceptor messageTraceInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                SimpMessageHeaderAccessor headers =
                        SimpMessageHeaderAccessor.wrap(message);
                log.info("消息流向: {} -> {}",
                        headers.getDestination(),
                        headers.getSessionId());
                return message;
            }
        };
    }
}
