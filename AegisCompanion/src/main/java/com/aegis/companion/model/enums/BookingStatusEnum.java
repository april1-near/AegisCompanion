package com.aegis.companion.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 预约状态枚举
@Getter
@AllArgsConstructor
public enum BookingStatusEnum implements EnumCode {
    PENDING("PENDING", "待审批", "status-pending"),
    APPROVED("APPROVED", "已通过", "status-approved"),
    REJECTED("REJECTED", "已拒绝", "status-rejected"),
    COMPLETED("COMPLETED", "已完成", "status-completed"),
    CANCELED("CANCELED", "已取消", "status-canceled");

    private final String code;
    private final String description;
    private final String styleClass;

    public static BookingStatusEnum getByCode(String code) {
        for (BookingStatusEnum value : values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return value;
            }
        }
        return null;
    }
}
