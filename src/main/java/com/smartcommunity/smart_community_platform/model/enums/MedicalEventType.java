package com.smartcommunity.smart_community_platform.model.enums;

// file: enums/MedicalEventType.java
public enum MedicalEventType implements EnumCode {
    APPOINTMENT_CREATED("CREATED", "预约创建"),
    APPOINTMENT_CANCELED("CANCELED", "预约取消"),
    APPOINTMENT_CONFIRMED("CONFIRMED", "就诊确认"),
    APPOINTMENT_REMINDER("REMINDER", "就诊提醒");

    private final String code;
    private final String description;

    MedicalEventType(String code, String description) {
        this.code = code;
        this.description = description;
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
