package com.smartcommunity.smart_community_platform.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class AppointmentVO {
    private Long id;
    private long userId;
    private String doctorName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointDate;
    private String timeSlot;
    private String statusDesc; // 状态描述/Description du statut

}
