package com.smartcommunity.smart_community_platform.model.enums;// Role.java

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum Role implements EnumCode {
    USER("USER", "普通用户"),
    MAINTENANCE("MAINTENANCE", "维修人员"),
    ADMIN("ADMIN", "管理员"),
    SUPER_ADMIN("SUPER_ADMIN", "超级管理员");

    @EnumValue
    private final String code;
    private final String description;

    Role(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
