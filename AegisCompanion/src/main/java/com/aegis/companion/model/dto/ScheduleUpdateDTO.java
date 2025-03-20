package com.aegis.companion.model.dto;

import com.aegis.companion.model.enums.TimeSlotStatusEnum;
import com.aegis.companion.utils.TimeSlotsTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
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
