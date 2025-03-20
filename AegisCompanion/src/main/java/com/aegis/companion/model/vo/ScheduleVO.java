package com.aegis.companion.model.vo;

import com.aegis.companion.model.enums.TimeSlotStatusEnum;
import com.aegis.companion.utils.TimeSlotsTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
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