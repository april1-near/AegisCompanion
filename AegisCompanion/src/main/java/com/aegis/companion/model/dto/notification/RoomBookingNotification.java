package com.aegis.companion.model.dto.notification;

import com.aegis.companion.model.enums.BookingStatusEnum;
import com.aegis.companion.model.vo.BookingRecordVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
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
