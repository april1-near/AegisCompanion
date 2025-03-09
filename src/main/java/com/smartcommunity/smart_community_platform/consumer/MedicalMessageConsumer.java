package com.smartcommunity.smart_community_platform.consumer;

import com.smartcommunity.smart_community_platform.model.dto.notification.MedicalNotification;
import com.smartcommunity.smart_community_platform.service.MessageTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MedicalMessageConsumer {

    private final MessageTemplateService templateService;
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "medical.queue", messageConverter = "jsonMessageConverter")
    public void processActivityEvent(@Payload MedicalNotification notification) {
        // 获取消息模板
        String template = templateService.getTemplate(
                notification.getTemplateCode()
        );

        // 格式化消息
        String message = String.format(
                template,
                notification.getDoctorName(),
                notification.getAppointDate(),
                notification.getTimeSlot()
        );

        // 推送到前端
        messagingTemplate.convertAndSendToUser(
                String.valueOf(notification.getUserId()),
                "/queue/medical/notifications",
                message);

        log.info("medical消费者：通知已发出：{}",message);
    }


}
