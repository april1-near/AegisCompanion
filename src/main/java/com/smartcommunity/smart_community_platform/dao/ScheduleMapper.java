package com.smartcommunity.smart_community_platform.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcommunity.smart_community_platform.model.entity.Schedule;
import com.smartcommunity.smart_community_platform.utils.TimeSlotsTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

public interface ScheduleMapper extends BaseMapper<Schedule> {

    // 按医生+日期查询排班

    @Select("SELECT * FROM schedule " +
            "WHERE doctor_id = #{doctorId} " +
            "AND work_date = #{date}")
    @Results({
            @Result(column = "time_slots", property = "timeSlots",
                    typeHandler = TimeSlotsTypeHandler.class)
    })
    Schedule selectByDoctorAndDate(@Param("doctorId") Long doctorId,
                                   @Param("date") LocalDate date);
}
