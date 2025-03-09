package com.smartcommunity.smart_community_platform.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartcommunity.smart_community_platform.model.enums.TimeSlotStatusEnum;
import com.smartcommunity.smart_community_platform.utils.TimeSlotsTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Map;

@Data
@Accessors(chain = true)
@TableName(value = "schedule", autoResultMap = true)
public class Schedule {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long doctorId;      // 医生ID/ID médecin
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate workDate; // 排班日期/Date de planning

    @TableField(typeHandler = TimeSlotsTypeHandler.class)
    private Map<String, TimeSlotStatusEnum> timeSlots;
    // JSON字段映射示例: {"09:00-10:00": "available"}
}