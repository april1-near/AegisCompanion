package com.aegis.companion.model.entity;


import com.aegis.companion.model.enums.TicketState;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 状态机持久化记录实体
 */

/**
 * 状态机运行时记录实体
 */
@Data
@TableName("state_machine_record")
public class StateMachineRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 状态机ID（与工单ID绑定）
     */
    private String machineId;

    /**
     * 当前状态（TicketState枚举值）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private TicketState state;

    /**
     * 上下文数据（JSON格式）
     */
    private String contextJson;

    @Version
    private Integer version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
