package com.smartcommunity.smart_community_platform.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

// RoomTypeEnum.java
@Getter
@AllArgsConstructor
public enum RoomTypeEnum {
    MEETING("MEETING", "会议室"),
    SPORTS("SPORTS", "运动场馆"),
    ENTERTAINMENT("ENTERTAINMENT", "娱乐设施");

    @EnumValue // 标记存储到数据库的字段
    @JsonValue
    private final String code;
    private final String description;

    @JsonCreator // 反序列化构造器
    public static RoomTypeEnum fromCode(String code) {
        for (RoomTypeEnum type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的活动室类型: " + code);
    }
}
