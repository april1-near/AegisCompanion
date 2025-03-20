package com.aegis.companion.dao;

import com.aegis.companion.model.entity.TicketLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 工单日志Mapper
 */
public interface TicketLogMapper extends BaseMapper<TicketLog> {

    /**
     * 批量查询工单日志
     */
    @Select("<script>" +
            "SELECT * FROM ticket_log WHERE ticket_id IN " +
            "<foreach item='id' collection='ticketIds' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<TicketLog> selectByTicketIds(@Param("ticketIds") List<Long> ticketIds);
}
