package com.aegis.companion.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(chain = true)
public enum AppointmentStatusEnum implements EnumCode {
    PENDING("pending", "待确认"),
    CONFIRMED("confirmed", "已确认"),
    CANCELED("canceled", "已取消");


    @EnumValue
    private final String code;
    private final String description;

    // 构造方法及getter
}