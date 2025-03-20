package com.aegis.companion.dao;

import com.aegis.companion.model.entity.CommunityRoom;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CommunityRoomMapper extends BaseMapper<CommunityRoom> {
    // 自定义分页查询（包含活动室类型过滤）
    IPage<CommunityRoom> selectByType(Page<?> page, @Param("type") String roomType);

    @Select("SELECT COUNT(*) FROM `community_room` WHERE room_name = #{name}")
    boolean existsByName(String name);

    @Update("UPDATE `community_room` SET is_active = #{active} WHERE id = #{roomId}")
    int updateStatus(@Param("roomId") Long roomId, @Param("active") boolean active);
    @Select("SELECT * FROM community_room WHERE is_active = 1 ")
    List<CommunityRoom> selectAvailableRooms();

}