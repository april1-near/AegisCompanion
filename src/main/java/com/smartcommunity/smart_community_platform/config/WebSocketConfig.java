package com.smartcommunity.smart_community_platform.config;

import com.smartcommunity.smart_community_platform.security.JwtHandshakeHandler;
import com.smartcommunity.smart_community_platform.utils.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;

    public WebSocketConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * 创建WebSocket心跳任务调度器
     * @return 配置好的ThreadPoolTaskScheduler
     */
    @Bean(name = "messageBrokerTaskScheduler")
    public ThreadPoolTaskScheduler messageBrokerTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ws-heartbeat-thread-");
        scheduler.setDaemon(true);
        return scheduler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-endpoint")
                .setHandshakeHandler(new JwtHandshakeHandler(jwtUtil))
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setDisconnectDelay(30 * 1000)
                .setHeartbeatTime(25 * 1000)
                .setSessionCookieNeeded(false)
                .setWebSocketEnabled(true)
                .setStreamBytesLimit(512 * 1024); // 512KB
    }

    // 配置消息代理（关键调整点）
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用内存消息代理（用于前端订阅）
        registry.enableSimpleBroker("/topic", "/queue")
                .setTaskScheduler(messageBrokerTaskScheduler()) // 使用Bean配置的调度器
                .setHeartbeatValue(new long[]{10000, 10000}); // 设置心跳间隔

        // 设置应用前缀（客户端发送消息到服务端的路径）
        registry.setApplicationDestinationPrefixes("/app");

        // 设置用户专属前缀（自动转换 /user/{userId}/... 路径）
        registry.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(128 * 1024) // 128KB
                .setSendBufferSizeLimit(512 * 1024) // 512KB
                .setSendTimeLimit(20 * 1000); // 20 seconds
    }
}
