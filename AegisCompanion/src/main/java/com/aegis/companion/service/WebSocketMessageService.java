package com.aegis.companion.service;

import com.aegis.companion.model.dto.notification.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageTemplateService templateService;

    // region RabbitMQ 监听器
    @RabbitListener(queues = "medical.queue")
    public void handleMedical(MedicalNotification notification) {
        processNotification(notification, "medical");
    }

    @RabbitListener(queues = "parking.queue")
    public void handleParking(ParkingNotification notification) {
        processNotification(notification, "parking");
    }

    @RabbitListener(queues = "ticket.queue")
    public void handleTicket(TicketNotification notification) {
        processNotification(notification, "ticket");
    }

    @RabbitListener(queues = "activity.queue")
    public void handleBooking(RoomBookingNotification notification) {
        processNotification(notification, "activity");
    }
    // endregion

    // region 核心处理方法
    private void processNotification(Notification notification, String module) {
        try {
//            validateSender(notification);
            String formattedContent = formatMessage(notification);
            Set<Long> recipients = resolveRecipients(notification);
            sendToUsers(recipients, module, formattedContent);
        } catch (Exception e) {
            log.error("消息处理失败: {} {}", module, notification, e);
        }
    }

    private String formatMessage(Notification notification) {
        Object[] params = resolveTemplateParams(notification);
        return String.format(
                templateService.getTemplate(notification.getTemplateCode()),
                params
        );
    }

    // endregion
    private Object[] resolveTemplateParams(Notification notification) {
        if (notification instanceof MedicalNotification medical) {
            return new Object[]{
                    medical.getDoctorName(),
                    medical.getAppointDate().format(DateTimeFormatter.ISO_DATE),
                    medical.getTimeSlot()
            };
        } else if (notification instanceof ParkingNotification parking) {
            return new Object[]{
                    parking.getSpaceId(),
                    parking.getExpireTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            };
        } else if (notification instanceof TicketNotification ticket) {
            return new Object[]{
                    ticket.getTitle(),
                    "#" + ticket.getTicketId(),
                    Optional.ofNullable(ticket.getRemake()).orElse("无备注"),
                    resolvePriorityText(ticket.getType())
            };
        } else if (notification instanceof RoomBookingNotification booking) {
            return new Object[]{
                    booking.getPurpose(),
                    booking.getTimeRange(),
                    Optional.ofNullable(booking.getRejectReason()).orElse("无拒绝原因")
            };
        } else {
            throw new IllegalArgumentException("未知通知类型: " + notification.getClass());
        }
    }

    private String resolvePriorityText(String typeCode) {
        return switch (typeCode) {
            case "URGENT" -> "🔥 紧急";
            case "HIGH" -> "⚠️ 高";
            case "NORMAL" -> "◉ 普通";
            default -> "◌ 未指定";
        };
    }
    // endregion

    // region 接收人处理（工单多接收人）
    private Set<Long> resolveRecipients(Notification notification) {
        Set<Long> recipients = new LinkedHashSet<>();
        recipients.add(notification.getUserId());

        if (notification instanceof TicketNotification ticket) {
            Stream.of(
                    ticket.getAssigneeId(),
                    ticket.getReviewerId()
            ).filter(Objects::nonNull).forEach(recipients::add);
        }

        return recipients;
    }
    // endregion

    // region 消息发送（增强结构）
    private void sendToDestination(Long userId, String module, String content) {
        String destination = "/queue/messages";

        Map<String, Object> message = new LinkedHashMap<>(4);
        message.put("type", "NOTIFICATION");
        message.put("content", content);
        message.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        message.put("module", module);

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                destination,
                message
        );
    }

    private void sendToUsers(Set<Long> userIds, String module, String content) {
        userIds.forEach(userId -> sendToDestination(userId, module, content));
        log.info("发送消息 => 用户[{}] 模块[{}] 内容: {}", userIds, module, content);
    }
    // endregion
}
