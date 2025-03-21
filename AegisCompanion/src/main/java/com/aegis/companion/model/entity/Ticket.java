package com.aegis.companion.model.entity;

import com.aegis.companion.model.enums.TicketState;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.apache.ibatis.type.EnumTypeHandler;

import java.time.LocalDateTime;

/**
 * 工单主实体
 */
@Data
@TableName("ticket")
public class Ticket {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String type;

    /**
     * 工单状态（枚举字符串存储）
     */
    @TableField(typeHandler = EnumTypeHandler.class)
    private TicketState state;

    private Long userId;
    private Long assigneeId;
    private Long reviewerId;
    private LocalDateTime confirmTime;
    private LocalDateTime closeTime;

    @Version
    private Integer version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String creatorName;

    @TableField(exist = false)
    private String assigneeName;
}