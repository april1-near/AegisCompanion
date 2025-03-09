package com.smartcommunity.smart_community_platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableAsync 
@SpringBootApplication
@MapperScan("com.smartcommunity.smart_community_platform.dao") // 注意：这是唯一的Mapper扫描配置，不要在其他配置类中重复此注解
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class SmartCommunityPlatformApplication {

    public static void main(String[] args) {
        // 允许Bean覆盖，解决WebSocket相关配置冲突
        // 这允许 WebSocketConfig 中的配置优先于 Spring 内部默认配置
        SpringApplication application = new SpringApplication(SmartCommunityPlatformApplication.class);
        application.setAllowBeanDefinitionOverriding(true);
        application.run(args);
    }

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .failOnUnknownProperties(true);
    }
}
