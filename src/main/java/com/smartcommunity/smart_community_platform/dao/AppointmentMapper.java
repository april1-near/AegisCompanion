package com.smartcommunity.smart_community_platform.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcommunity.smart_community_platform.model.entity.Appointment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

public interface AppointmentMapper extends BaseMapper<Appointment> {

    // 检查是否存在相同预约
    @Select("SELECT COUNT(*) FROM `appointment` WHERE doctor_id=#{doctorId} " +
            "AND appoint_date=#{date} AND time_slot=#{slot}")
    int checkDuplicate(@Param("doctorId") Long doctorId,
                       @Param("date") LocalDate date,
                       @Param("slot") String slot);
}
