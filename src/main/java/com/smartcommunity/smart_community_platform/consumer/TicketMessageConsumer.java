package com.smartcommunity.smart_community_platform.consumer;

import com.smartcommunity.smart_community_platform.model.dto.notification.TicketNotification;
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
public class TicketMessageConsumer {

    private final MessageTemplateService templateService;
    private final SimpMessagingTemplate messagingTemplate;


    @RabbitListener(queues = "ticket.queue", messageConverter = "jsonMessageConverter")
    public void processTicketEvent(@Payload TicketNotification notification) {

        log.info("ticket消费者：通知已发出：{}",notification);

        // 根据模板类型构建消息内容
        String message = buildMessage(notification);

        // 获取目标用户ID
        String targetUserId = getTargetUserId(notification);

        // 推送到前端
        if (targetUserId != null) {
            messagingTemplate.convertAndSendToUser(
                    targetUserId,
                    "/queue/ticket/notifications",
                    message
            );
            log.info("ticket消费者：发出去了呱{}",message);

        }
    }

    private String buildMessage(TicketNotification notification) {
        String templateCode = notification.getTemplateCode();
        String template = templateService.getTemplate(templateCode);

        switch (templateCode) {
            case "TICKET_AUTO_ASSIGNED":
                return String.format(template,
                        notification.getTicketId(),
                        notification.getType(),
                        notification.getTimestamp());

            case "TICKET_REVIEW_PENDING":
                return String.format(template,
                        notification.getTicketId(),
                        notification.getAssigneeId());

            case "TICKET_REVIEW_FAILED":
                return String.format(template,
                        notification.getTicketId(),
                        notification.getRemake());

            case "TICKET_COMPLETED":
                return String.format(template, notification.getTicketId());

            default:
                return "未知通知类型";
        }
    }

    private String getTargetUserId(TicketNotification notification) {
        String templateCode = notification.getTemplateCode();
        switch (templateCode) {
            case "TICKET_AUTO_ASSIGNED":
            case "TICKET_REVIEW_FAILED":
                return String.valueOf(notification.getAssigneeId());

            case "TICKET_REVIEW_PENDING":
                return String.valueOf(notification.getReviewerId());

            case "TICKET_COMPLETED":
                return String.valueOf(notification.getUserId());

            default:
                return null;
        }
    }
}
