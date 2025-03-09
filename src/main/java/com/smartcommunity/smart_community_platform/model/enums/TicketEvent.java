package com.smartcommunity.smart_community_platform.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TicketEvent {
    AUTO_ASSIGN(" AUTO_ASSIGN", "自动分配"),     // 自动分配
    MANUAL_PROCESS("MANUAL_PROCESS", "工人处理"), // 人工处理
    SUBMIT_REVIEW("SUBMIT_REVIEW", "提交审核"),   // 提交审核
    REVIEW_PASS("REVIEW_PASS", "审核通过"),   // 审核通过
    REVIEW_REJECT("REVIEW_REJECT", "审核驳回"),  // 审核驳回
    USER_CONFIRM("USER_CONFIRM", "用户确认"),  // 用户确认
    SYSTEM_CLOSE("SYSTEM_CLOSE", "系统关闭");    // 系统关闭
    @EnumValue
    private final String code;
    private final String description;
}
