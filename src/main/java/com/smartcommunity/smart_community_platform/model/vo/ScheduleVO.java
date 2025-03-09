package com.smartcommunity.smart_community_platform.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartcommunity.smart_community_platform.model.enums.TimeSlotStatusEnum;
import com.smartcommunity.smart_community_platform.utils.TimeSlotsTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Map;

@Data
@Accessors(chain = true)
public class ScheduleVO {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate workDate;
    @TableField(typeHandler = TimeSlotsTypeHandler.class)
    private Map<String, TimeSlotStatusEnum> timeSlots;
}