package com.smartcommunity.smart_community_platform.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smartcommunity.smart_community_platform.model.enums.AppointmentStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.EnumTypeHandler;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
@TableName("appointment")
public class Appointment {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;        // 用户ID/ID utilisateur
    private Long doctorId;
    private LocalDate appointDate; // 预约日期/Date de rendez-vous
    private String timeSlot;     // 时间段/Créneau horaire
    @TableField(typeHandler = EnumTypeHandler.class)
    private AppointmentStatusEnum status;
}
