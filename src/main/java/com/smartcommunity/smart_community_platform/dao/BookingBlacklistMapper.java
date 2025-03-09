package com.smartcommunity.smart_community_platform.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcommunity.smart_community_platform.model.entity.BookingBlacklist;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

public interface BookingBlacklistMapper extends BaseMapper<BookingBlacklist> {
    // 查询有效黑名单记录
    @Select("SELECT COUNT(*) FROM `booking_blacklist` " +
            "WHERE user_id = #{userId} " +
            "AND expire_time > NOW()")
    boolean existsValidRecord(@Param("userId") Long userId,
                              @Param("now") LocalDateTime now);
}
