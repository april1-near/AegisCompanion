package com.smartcommunity.smart_community_platform.model.dto.notification;

import com.smartcommunity.smart_community_platform.model.enums.BookingStatusEnum;
import com.smartcommunity.smart_community_platform.model.vo.BookingRecordVO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoomBookingNotification extends Notification {
    // 业务参数
    private String purpose;
    private String timeRange;
    private String rejectReason;

    public RoomBookingNotification(long userId, String eventType, LocalDateTime timestamp, String templateCode) {
        super(userId, eventType, timestamp, templateCode);
    }

    public static RoomBookingNotification build(BookingStatusEnum type, BookingRecordVO bookingRecordVO) {
        {
            RoomBookingNotification notification = new RoomBookingNotification(
                    bookingRecordVO.getUserId(),
                    type.getCode(),
                    LocalDateTime.now(),
                    "BOOKING_" + type.getCode().toUpperCase());

            notification.setPurpose(bookingRecordVO.getPurpose());
            notification.setRejectReason(bookingRecordVO.getRejectReason());
            notification.setTimeRange(bookingRecordVO.getTimeRange());

            return notification;
        }
    }


}
