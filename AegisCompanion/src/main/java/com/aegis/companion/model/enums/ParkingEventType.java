// 文件：ParkingOperationType.java
package com.aegis.companion.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 车位操作类型枚举
 */
@Getter
@AllArgsConstructor
public enum ParkingEventType {
    RESERVE("reserve", "预约车位"),
    RESERVE_CANCELED("reserve_canceled", "预约取消"),
    OCCUPY("occupy", "车辆到达"),
    RELEASE("release", "释放车位"),
    ADMIN_RELEASE("admin_release", "管理员强制释放"),
    TIMEOUT_RELEASE("timeout_release", "超时自动释放");

    @EnumValue
    private final String code;
    private final String description;
}
