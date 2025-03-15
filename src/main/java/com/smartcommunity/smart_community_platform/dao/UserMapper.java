package com.smartcommunity.smart_community_platform.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcommunity.smart_community_platform.model.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    /**
     * 通过用户名+ID联合查询
     */
// 方法参数直接命名为 id
    @Select("SELECT * FROM `user` WHERE username = #{username} AND id = #{id}")
    User selectByUsernameAndId(@Param("username") String username, @Param("id") Long id);


    // 方法参数直接命名为 id
    @Select("SELECT * FROM `user` WHERE username = #{username} ")
    User findByUsername(@Param("username") String username);
    //===========================


    @Update("UPDATE user SET current_load = current_load + #{delta}, " +
            "version = version + 1 WHERE id = #{workerId} AND version = #{version}")
    int updateLoadWithLock(@Param("workerId") Long workerId,
                           @Param("delta") int delta,
                           @Param("version") Integer version);

    @Select("SELECT * FROM user WHERE role = 'MAINTENANCE' " +
            "AND JSON_CONTAINS(skills, JSON_QUOTE(#{skill})) " + // 关键修复：添加JSON_QUOTE
            "AND is_enabled = 1")
    List<User> selectAvailableWorkers(String skill);
}

