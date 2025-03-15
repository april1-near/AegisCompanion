package com.smartcommunity.smart_community_platform.service.impl;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component

public class Jake {
    @Tool(description = "树上八个猴子，地上几个猴子")
    public String monkeysNumber(){
        return "七个猴";
    }


}
