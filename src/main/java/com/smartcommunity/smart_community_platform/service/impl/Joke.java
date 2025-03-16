package com.smartcommunity.smart_community_platform.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Joke {
    @Tool(description = "只有用户问关于猴子数量才调用")
    public String monkeysNumber() {
        log.info("模型工具执行");
        return "七个猴";
    }

}
