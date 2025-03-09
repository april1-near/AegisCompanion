package com.smartcommunity.smart_community_platform.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 工单类型-技能映射实体
 */
@Data
@TableName("ticket_type_skill")
public class TicketTypeSkill {
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 工单类型（唯一）
     */
    @TableField("type")
    private String type;

    /**
     * 所需技能标签
     */
    @TableField("required_skill")
    private String requiredSkill;
}

