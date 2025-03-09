package com.smartcommunity.smart_community_platform.model.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(chain = true)
public enum TimeSlotStatusEnum implements EnumCode {
    AVAILABLE("available", "可预约/Disponible"),
    BOOKED("booked", "已预约/Réservé"),
    CLOSED("closed", "已关闭/Fermé");

    // 同上
    @EnumValue
    private final String code;
    private final String description;

    @JsonCreator  // 反序列化时匹配code值
    public static TimeSlotStatusEnum fromCode(String code) {
        for (TimeSlotStatusEnum status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效状态: " + code);
    }

    @JsonValue  // 序列化时使用code值
    public String getCode() {
        return code;
    }

}
