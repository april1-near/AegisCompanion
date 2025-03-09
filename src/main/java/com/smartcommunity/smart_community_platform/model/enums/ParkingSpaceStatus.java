package com.smartcommunity.smart_community_platform.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.Arrays;

// 停车位状态枚举
// 文件：ParkingSpaceStatus.java
public enum ParkingSpaceStatus implements EnumCode {
    FREE("FREE", "空闲可预约"),
    RESERVED("RESERVED", "已预约未使用"),
    OCCUPIED("OCCUPIED", "已占用");

    @EnumValue
    private final String code;
    private final String description;

    ParkingSpaceStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ParkingSpaceStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效状态码: " + code));
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
