package com.smartcommunity.smart_community_platform.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BlacklistReasonEnum implements EnumCode {
    REPEAT_CANCEL("REPEAT_CANCEL", "频繁取消预约"),
    OVERDUE_USE("OVERDUE_USE", "超时占用场地"),
    VIOLATE_RULE("VIOLATE_RULE", "违反使用规定");

    @EnumValue
    private final String code;
    private final String description;
}
