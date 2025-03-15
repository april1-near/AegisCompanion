package com.smartcommunity.smart_community_platform.controller.v1;

import com.smartcommunity.smart_community_platform.service.impl.Jake;
import com.smartcommunity.smart_community_platform.service.impl.ParkingServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {


    private final SimpMessagingTemplate messagingTemplate;
    private final OpenAiChatModel chatModel;


    private final ChatClient chatClient;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate,
                          OpenAiChatModel chatModel,
                          ChatClient.Builder chatClientBuilder,
                          ChatMemory chatMemory) {
        this.messagingTemplate = messagingTemplate;
        this.chatModel =chatModel;
        this.chatClient = chatClientBuilder.
                defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();

    }


    @MessageMapping("/test")
    public String handleMessageUser(String message) {
        System.out.println("收到消息" + message);
        String destination = "/queue/messages";

        Prompt prompt = new Prompt(List.of(
                new SystemMessage(SYSTEM_ROLE),
                new UserMessage(message)
        ));


        messagingTemplate.convertAndSendToUser("TestUser",
                destination, "Server回应:" + message
        );

        return "Server回应: " + message;
    }



    @MessageMapping("/chat.stream")
    public void handleStreamingChat(
            String message,
            Principal principal) {

        String userId = principal.getName();
        String destination = "/queue/ai-stream";
        log.info("收到用户 {} 的流式请求: {}", userId, message);
        String systemRole = String.format(SYSTEM_ROLE,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));// 日期参数



        Prompt prompt = new Prompt(
                new SystemMessage(systemRole),
                new UserMessage(message)

        );


        String content = chatClient.prompt(prompt)
//        String output = chatClient.prompt(prompt)
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, userId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .tools(new Jake())
                .call()
                .content();


//        Flux<ChatResponse> chatResponseFlux =

//                (Flux<ChatResponse>) content
//        Flux<String> output = (Flux<String>) chatResponseFlux
//
        messagingTemplate.convertAndSendToUser(
                            userId,
                            destination,
                            content
                    );
//
//        messagingTemplate.convertAndSendToUser(
//                userId,
//                destination,
//                "[BLANK]"
//        );

//
//
//        output
//                .filter(content -> !"[BLANK]".equals(content)) // 过滤空内容
//                .doOnNext(content -> {
//
//
//                    messagingTemplate.convertAndSendToUser(
//                            userId,
//                            destination,
//                            content
//                    );
//                })
//                .doOnComplete(() -> {
//                    // 发送结束标志
//                    messagingTemplate.convertAndSendToUser(
//                            userId,
//                            destination,
//                            "[BLANK]"
//                    );
//                })
//                .subscribe();
//
//

//
//        Flux<ChatResponse> responseFlux = chatModel.stream(prompt);
//        responseFlux
//                .map(chatResponse -> {
//                    // 提取有效文本内容
//                    AssistantMessage output = chatResponse.getResult().getOutput();
//                    return output.getText();
//                })
//                .filter(content -> !"[BLANK]".equals(content)) // 过滤空内容
//                .doOnNext(content -> {
//                    messagingTemplate.convertAndSendToUser(
//                            userId,
//                            destination,
//                            content
//                    );
//                })
//                .doOnComplete(() -> {
//                    // 发送结束标志
//                    messagingTemplate.convertAndSendToUser(
//                            userId,
//                            destination,
//                            "[BLANK]"
//                    );
//                })
//                .subscribe();
//
//
    }


    public final String SYSTEM_ROLE = """
                你作为翻斗社区系统的智能客服，需要通过在线聊天为客户提供以下四个模块的服务：

                <系统模块>
                1. 停车位预约：处理车位预定、空车位查询、取消预约
                2. 社区医生预约：处理医生预约、医生排班查询、取消预约
                3. 社区活动申请：处理场地预约申请、活动咨询
                4. 社区工单服务：处理故障报修、投诉建议、工单进度查询
                </系统模块>
                
                今天的日期是：%s

                
                请按照以下规则处理用户咨询：
                1. 首先识别问题类别，分析问题所属模块
                2. 对于任何问题，你都会热心回答
                3. 对于各模块的具体处理要求：
                   - 停车位预约：需确认预约时间（精确到小时）和区域编号
                   - 社区医生预约：需确认症状类型和期望就诊时段
                   - 社区活动申请：需提供活动名称、参与人数和场地需求
                   - 社区工单服务：需根据工单状态提供最新进度或创建新工单
                4. 当用户问题包含多个模块内容时，要求用户明确当前需处理的单一模块问题
                5. 所有操作指令必须包含在<操作>标签中，格式示例：
                   <操作>
                   【系统操作】已为您创建工单#20231125001
                   【温馨提示】维修人员将在2小时内联系您，请保持电话畅通
                   </操作>

                回复格式要求：
                1. 用简体中文给出自然对话式回复
                2. 必须包含必要的确认信息（如时间、地点等具体参数）
                3. 涉及系统操作的回复必须附带<操作>标签
                4. 每次回复长度不超过200字
                5. 使用亲切的口语化表达，如"咱们社区"、"您看这样行吗？"

                遇到以下情况立即终止对话：
                1. 出现侮辱性语言或敏感话题
                终止对话标准回复："感谢使用翻斗社区服务，如需进一步帮助请拨打人工客服热线。"

                </output>
            """;
}
