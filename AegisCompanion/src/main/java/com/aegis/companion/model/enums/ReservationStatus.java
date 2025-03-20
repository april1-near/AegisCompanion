// 文件：ReservationStatus.java
package com.aegis.companion.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 预约记录状态枚举
 * 映射 parking_reservation.status 字段
 */
@Getter
@AllArgsConstructor
public enum ReservationStatus implements EnumCode {
    ACTIVE("active", "生效中"),
    ARRIVED("arrived", "已到达车位"), // 新增状态
    COMPLETED("completed", "已完成使用"),
    EXPIRED("expired", "已过期"),
    CANCELED("canceled", "已取消");
    @EnumValue  // MP注解，指定存入数据库的值
    private final String code;
    private final String description;

    public static ReservationStatus fromCode(String code) {
        for (ReservationStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效状态码: " + code);
    }
}
