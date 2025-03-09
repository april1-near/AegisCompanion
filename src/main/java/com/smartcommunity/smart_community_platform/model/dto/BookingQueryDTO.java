package com.smartcommunity.smart_community_platform.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class BookingQueryDTO {
    private String status;
    private Long roomId;
    private LocalDateTime startTimeBegin;
    private LocalDateTime startTimeEnd;
}