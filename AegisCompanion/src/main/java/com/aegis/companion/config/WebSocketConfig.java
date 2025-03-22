package com.aegis.companion.config;

// file: config/WebSocketConfig.java

import com.aegis.companion.security.WebSocketAuthInterceptor;
import com.aegis.companion.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

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
        // 启用 RabbitMQ 作为外部 STOMP 代理（替代内存代理）
        registry.enableStompBrokerRelay("/queue", "/topic")
                .setRelayHost(System.getenv("SPRING_RABBITMQ_HOST"))          // RabbitMQ 服务器地址
                .setRelayPort(61613)                // STOMP 协议默认端口
                .setVirtualHost("/")                // 虚拟主机（根据实际情况配置）
                .setClientLogin("guest")            // RabbitMQ 用户名
                .setClientPasscode("guest");        // RabbitMQ 密码

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
                .setHeartbeatTime(5000);// 设置心跳间隔

        registry.addEndpoint("/ws-native") // 新增原生WebSocket端点
                .setHandshakeHandler(webSocketAuthInterceptor())
                .setAllowedOriginPatterns("*");

    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        // 添加自定义转换器
        messageConverters.add(new MappingJackson2MessageConverter());
        messageConverters.add(new StringMessageConverter());
        return false; // 不使用默认转换器
    }

    @Bean
    public WebSocketAuthInterceptor webSocketAuthInterceptor() {
        return new WebSocketAuthInterceptor();
    }

}
