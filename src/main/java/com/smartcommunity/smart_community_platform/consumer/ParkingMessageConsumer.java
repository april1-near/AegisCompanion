package com.smartcommunity.smart_community_platform.consumer;

import com.smartcommunity.smart_community_platform.model.dto.notification.ParkingNotification;
import com.smartcommunity.smart_community_platform.service.MessageTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParkingMessageConsumer {

    private final MessageTemplateService templateService;
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "parking.queue", messageConverter = "jsonMessageConverter")
    public void processParkingEvent(@Payload ParkingNotification notification) {
        // 获取消息模板
        String template = templateService.getTemplate(
                notification.getTemplateCode()
        );

        // 格式化消息
        String message = String.format(template,
                notification.getSpaceId(),
                notification.getExpireTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );


        // 推送到前端
        messagingTemplate.convertAndSendToUser(
                String.valueOf(notification.getUserId()),
                "/queue/parking/notifications",
                message);
        log.info("parking消费者：通知已发出：{}",message);
    }

}