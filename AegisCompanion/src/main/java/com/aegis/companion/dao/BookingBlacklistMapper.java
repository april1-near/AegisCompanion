package com.aegis.companion.dao;

import com.aegis.companion.model.entity.BookingBlacklist;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
