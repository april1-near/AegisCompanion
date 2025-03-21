package com.aegis.companion.dao;

import com.aegis.companion.model.entity.Ticket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 工单Mapper
 */
public interface TicketMapper extends BaseMapper<Ticket> {

    @Select("SELECT * FROM ticket WHERE id = #{id} FOR UPDATE")
    Ticket selectByIdWithLock(Long id);


    //==============================
    // 带版本号更新
    @Update("UPDATE ticket SET state=#{state},assignee_id=#{assigneeId},version=version+1 WHERE id=#{id} AND version=#{version}")
    int updateByIdWithVersion(Ticket ticket);

    @Select("SELECT * FROM ticket WHERE id = #{ticketId} FOR UPDATE")
    Ticket selectForUpdate(Long ticketId);

    @Update("UPDATE ticket SET state = #{state}, assignee_id = #{assigneeId}, " +
            "version = version + 1 WHERE id = #{id} AND version = #{version}")
    int updateAssignmentWithLock(Ticket ticket);


    //====================

    /**
     * 根据用户ID查询工单及创建人/处理人姓名
     */
    @Select("SELECT t.*, u1.username as creator_name, u2.username as assignee_name " +
            "FROM ticket t " +
            "LEFT JOIN user u1 ON t.user_id = u1.id " +
            "LEFT JOIN user u2 ON t.assignee_id = u2.id " +
            "WHERE t.user_id = #{userId}")
    List<Ticket> selectByUserIdWithNames(@Param("userId") Long userId);

    /**
     * 根据处理人ID查询工单及创建人/处理人姓名
     */
    @Select("SELECT t.*, u1.username as creator_name, u2.username as assignee_name " +
            "FROM ticket t " +
            "LEFT JOIN user u1 ON t.user_id = u1.id " +
            "LEFT JOIN user u2 ON t.assignee_id = u2.id " +
            "WHERE t.assignee_id = #{assigneeId}")
    List<Ticket> selectByAssigneeIdWithNames(@Param("assigneeId") Long assigneeId);


    @Select("SELECT t.*, u1.username as creator_name, u2.username as assignee_name " +
            "FROM ticket t " +
            "LEFT JOIN user u1 ON t.user_id = u1.id " +
            "LEFT JOIN user u2 ON t.assignee_id = u2.id " +
            "ORDER BY t.create_time DESC")
    List<Ticket> selectAllWithNames();


}