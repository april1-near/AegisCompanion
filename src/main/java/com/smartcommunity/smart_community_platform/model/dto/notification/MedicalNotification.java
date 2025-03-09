package com.smartcommunity.smart_community_platform.model.dto.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartcommunity.smart_community_platform.model.enums.MedicalEventType;
import com.smartcommunity.smart_community_platform.model.vo.AppointmentVO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MedicalNotification extends Notification {

    // 业务参数
    //医生名字
    private String doctorName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    //预约日期
    private LocalDate appointDate;
    //时间段
    private String timeSlot;

    public static MedicalNotification build(MedicalEventType type,
                                            AppointmentVO appointmentVO) {
        MedicalNotification notification = new MedicalNotification();
        notification.setUserId(appointmentVO.getUserId());
        notification.setEventType(type.getCode());
        notification.setTimestamp(LocalDateTime.now());
        notification.setTemplateCode("MEDICAL_" + type.getCode().toUpperCase());
        notification.setDoctorName(appointmentVO.getDoctorName());
        notification.setTimeSlot(appointmentVO.getTimeSlot());
        return notification;
    }
}
