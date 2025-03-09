package com.smartcommunity.smart_community_platform.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.smart_community_platform.model.entity.ParkingReservation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

public interface ParkingReservationMapper extends BaseMapper<ParkingReservation> {
    // 使用乐观锁更新
    @Update("UPDATE `parking_reservation` SET status=#{status}, version=version+1 WHERE id=#{id} AND version=#{version}")
    int updateStatusWithLock(@Param("id") Long id,
                             @Param("status") String status,
                             @Param("version") Integer version);

    // 文件：ParkingReservationMapper.java
    @Select("SELECT * FROM `parking_reservation` " +
            "WHERE user_id = #{userId} " +
            "AND status = 'active' " +
            "AND expire_time > #{now} " +
            "ORDER BY expire_time ASC LIMIT 1")
    ParkingReservation selectActiveByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);


    // 修改后的分页查询方法
    @Select(
            "SELECT * FROM `parking_reservation` " +
                    "WHERE status = 'active' " +
                    "AND expire_time < #{now} " +
                    "ORDER BY expire_time ASC"
    )
    IPage<ParkingReservation> selectExpiredReservationsPage(
            Page<ParkingReservation> page,
            @Param("now") LocalDateTime now
    );


    @Select("SELECT * FROM `parking_reservation` " +
            "WHERE space_id = #{spaceId} " +
            "AND status IN ('active', 'completed') " +
            "ORDER BY expire_time DESC LIMIT 1")
    ParkingReservation selectActiveBySpace(@Param("spaceId") Long spaceId);

}
