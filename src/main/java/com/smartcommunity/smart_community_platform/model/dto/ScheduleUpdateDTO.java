package com.smartcommunity.smart_community_platform.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartcommunity.smart_community_platform.model.enums.TimeSlotStatusEnum;
import com.smartcommunity.smart_community_platform.utils.TimeSlotsTypeHandler;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class ScheduleUpdateDTO {
    private Long doctorId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate workDate;
    @TableField(typeHandler = TimeSlotsTypeHandler.class)
    private Map<String, TimeSlotStatusEnum> timeSlots;
}
