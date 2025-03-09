package com.smartcommunity.smart_community_platform.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateDTO {
    @NotNull(message = "活动室ID不能为空")
    private Long roomId;

    @NotBlank(message = "用途说明不能为空")
    @Size(max = 200, message = "用途说明长度不能超过200字")
    private String purpose;

    @Min(value = 1, message = "参与人数至少1人")
    private Integer participantCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Future(message = "开始时间必须是将来的时间")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Future(message = "结束时间必须是将来的时间")
    private LocalDateTime endTime;
}
