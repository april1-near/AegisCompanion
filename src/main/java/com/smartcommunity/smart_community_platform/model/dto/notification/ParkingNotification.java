package com.smartcommunity.smart_community_platform.model.dto.notification;

import com.smartcommunity.smart_community_platform.model.entity.ParkingReservation;
import com.smartcommunity.smart_community_platform.model.enums.ParkingEventType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParkingNotification extends Notification {

    // 业务参数
    private Long spaceId;
    private LocalDateTime expireTime;

    public ParkingNotification(long userId, String eventType, LocalDateTime timestamp, String templateCode) {
        super(userId, eventType, timestamp, templateCode);
    }


    public static ParkingNotification build(ParkingEventType type,
                                            ParkingReservation reservation) {
        ParkingNotification vo = new ParkingNotification(
                reservation.getUserId(),
                type.getCode(),
                LocalDateTime.now(),
                "PARK_" + type.getCode().toUpperCase());

        vo.setSpaceId(reservation.getSpaceId());
        vo.setExpireTime(reservation.getExpireTime());
        return vo;
    }
}

