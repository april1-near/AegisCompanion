package com.smartcommunity.smart_community_platform.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum TicketState {
    CREATED("CREATED", "已创建"),
    AUTO_ASSIGNED("AUTO_ASSIGNED", "已自动分配"),
    PROCESSING("PROCESSING", "处理中"),
    REVIEW_PENDING("REVIEW_PENDING", "待审核"),
    REVIEW_PASSED("REVIEW_PASSED", "审核通过"),
    REVIEW_FAILED("REVIEW_FAILED", "审核未通过"),
    USER_CONFIRMED("USER_CONFIRMED", "用户已确认"),
    COMPLETED("COMPLETED", "已完成") {
        @Override
        public boolean isFinalState() {
            return true;
        }
    },
    CLOSED("CLOSED", "已关闭") {
        @Override
        public boolean isFinalState() {
            return true;
        }
    };

    @EnumValue  // 标记存储到数据库的字段
    private final String code;
    private final String desc;

    // 必须显式声明带参构造器
    TicketState(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public boolean isFinalState() {
        return false;
    }

    ;
}
