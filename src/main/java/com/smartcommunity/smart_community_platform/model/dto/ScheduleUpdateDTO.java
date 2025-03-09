package com.smartcommunity.smart_community_platform.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.smartcommunity.smart_community_platform.model.enums.TimeSlotStatusEnum;
import com.smartcommunity.smart_community_platform.utils.TimeSlotsTypeHandler;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class ScheduleUpdateDTO {
    private Long doctorId;      // 缺失关键字段
    private LocalDate workDate;
    @TableField(typeHandler = TimeSlotsTypeHandler.class)
    private Map<String, TimeSlotStatusEnum> timeSlots;
}
