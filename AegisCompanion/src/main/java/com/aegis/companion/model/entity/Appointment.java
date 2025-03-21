package com.aegis.companion.model.entity;

import com.aegis.companion.model.enums.AppointmentStatusEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
