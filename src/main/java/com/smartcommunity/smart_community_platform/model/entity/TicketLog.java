package com.smartcommunity.smart_community_platform.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smartcommunity.smart_community_platform.model.enums.TicketState;
import lombok.Data;
import org.apache.ibatis.type.EnumTypeHandler;

import java.time.LocalDateTime;

/**
 * 工单日志实体
 */
@Data
@TableName("ticket_log")
public class TicketLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ticketId;

    @TableField(typeHandler = EnumTypeHandler.class)
    private TicketState fromState;

    @TableField(typeHandler = EnumTypeHandler.class)
    private TicketState toState;

    private Long operatorId;
    private String remark;
    private LocalDateTime createTime;
}
