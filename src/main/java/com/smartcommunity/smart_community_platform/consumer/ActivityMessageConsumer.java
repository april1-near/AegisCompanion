package com.smartcommunity.smart_community_platform.consumer;

import com.smartcommunity.smart_community_platform.model.dto.notification.RoomBookingNotification;
import com.smartcommunity.smart_community_platform.service.MessageTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageConsumer {

    private final MessageTemplateService templateService;
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "activity.queue", messageConverter = "jsonMessageConverter")
    public void processActivityEvent(@Payload RoomBookingNotification notification) {

        // 获取消息模板
        String template = templateService.getTemplate(
                notification.getTemplateCode()
        );

        // 格式化消息
        String message = String.format(
                template,
                notification.getPurpose(),
                notification.getTimeRange(),
                notification.getRejectReason()
        );



        // 推送到前端
        messagingTemplate.convertAndSendToUser(
                String.valueOf(notification.getUserId()),
                "/queue/activity/notifications",
                message);
        log.info("activity消费者：通知已发出：{}",message);

    }


}
