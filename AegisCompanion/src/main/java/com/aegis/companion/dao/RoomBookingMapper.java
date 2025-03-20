package com.aegis.companion.dao;

import com.aegis.companion.model.dto.BookingQueryDTO;
import com.aegis.companion.model.entity.RoomBooking;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

public interface RoomBookingMapper extends BaseMapper<RoomBooking> {
    // 检测时间冲突（参数：roomId, startTime, endTime）
    Integer countTimeConflict(@Param("roomId") Long roomId,
                              @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);


    @Select("<script>" +
            "SELECT * FROM `room_booking` WHERE 1=1 " +
            "<if test='q.status != null'> AND booking_status = #{q.status} </if>" +
            "<if test='q.roomId != null'> AND room_id = #{q.roomId} </if>" +
            "ORDER BY create_time DESC" +
            "</script>")
    Page<RoomBooking> selectAdminBookings(Page<RoomBooking> page, @Param("q") BookingQueryDTO query);

    @Update("UPDATE `room_booking` SET booking_status = 'CANCELED' " +
            "WHERE room_id = #{roomId} AND start_time > NOW()")
    int cancelFutureBookings(Long roomId);

    @Select("SELECT COUNT(*) FROM `room_booking` " +
            "WHERE room_id = #{roomId} AND start_time > NOW()")
    boolean hasFutureBookings(Long roomId);


}