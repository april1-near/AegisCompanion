package com.smartcommunity.smart_community_platform.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcommunity.smart_community_platform.model.entity.TicketTypeSkill;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 工单类型技能Mapper
 */
public interface TicketTypeSkillMapper extends BaseMapper<TicketTypeSkill> {
    /**
     * 根据工单类型查询所需技能
     */
    @Select("SELECT required_skill FROM ticket_type_skill WHERE type = #{type}")
    String selectSkillByType(@Param("type") String type);
}
