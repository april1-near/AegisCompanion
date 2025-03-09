package com.smartcommunity.smart_community_platform.model.dto.notification;

import com.smartcommunity.smart_community_platform.model.enums.TicketState;
import com.smartcommunity.smart_community_platform.model.vo.TicketVO;
import com.smartcommunity.smart_community_platform.utils.BeanCopyUtils;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketNotification extends Notification {

    // 业务参数
    private Long ticketId;
    private Long assigneeId;
    private Long reviewerId;
    private String title;
    private String type;
    private String remake;


    public TicketNotification(long userId, String eventType, LocalDateTime timestamp, String templateCode) {
        super(userId, eventType, timestamp, templateCode);
    }

    public static TicketNotification build(TicketState state, TicketVO ticket) {
        TicketNotification notification = new TicketNotification(
                ticket.getUserId(),
                state.toString().toLowerCase(),
                LocalDateTime.now(),
                "TICKET_" + state.getCode()
        );

        BeanCopyUtils.copyNonNullProperties(ticket, notification);
        notification.setTicketId(ticket.getId());

        return notification;

    }

    @Override
    public String toString() {
        return "TicketNotification{" +
                super.toString() +
                "ticketId=" + ticketId +
                ", assigneeId=" + assigneeId +
                ", reviewerId=" + reviewerId +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", remake='" + remake + '\'' +
                '}';
    }


}


