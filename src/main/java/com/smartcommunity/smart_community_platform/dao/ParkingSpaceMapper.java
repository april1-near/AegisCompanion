package com.smartcommunity.smart_community_platform.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcommunity.smart_community_platform.model.entity.ParkingSpace;
import com.smartcommunity.smart_community_platform.model.enums.ParkingSpaceStatus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ParkingSpaceMapper extends BaseMapper<ParkingSpace> {
    /**
     * 根据区域查询空闲车位（带索引优化）
     *
     * @param zone 区域编码（必须大写字母）
     */
    @Select("SELECT * FROM `parking_space` " +
            "WHERE zone_code = #{zone} AND status = 'FREE' " +
            "ORDER BY number ASC")
    List<ParkingSpace> selectFreeSpacesByZone(String zone);

    /**
     * 带乐观锁的状态更新（XML实现）
     */
    int updateStatusWithLock(
            @Param("spaceId") Long spaceId,
            @Param("newStatus") ParkingSpaceStatus newStatus,
            @Param("oldVersion") Integer oldVersion
    );
}
