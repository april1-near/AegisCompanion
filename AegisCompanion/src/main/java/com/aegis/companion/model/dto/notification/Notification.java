package com.aegis.companion.model.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Notification {
    private long userId;
    private String eventType;        // 事件类型: RESERVE/CANCEL/EXPIRE
    private LocalDateTime timestamp; // 事件发生时间
    private String templateCode;     // 模板标识: PARKING_RESERVATION
}