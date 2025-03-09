package com.smartcommunity.smart_community_platform.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcommunity.smart_community_platform.model.entity.StateMachineRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 状态机记录Mapper
 */
public interface StateMachineRecordMapper extends BaseMapper<StateMachineRecord> {

    /**
     * 根据machine_id查询记录
     */
    @Select("SELECT * FROM state_machine_record WHERE machine_id = #{machineId}")
    StateMachineRecord selectByMachineId(@Param("machineId") String machineId);

    /**
     * 根据machine_id和版本号更新（乐观锁）
     */
    @Update("UPDATE state_machine_record SET state = #{state}, context_json = #{contextJson}, version = version + 1 WHERE machine_id = #{machineId} AND version = #{version}")
    int updateByMachineId(StateMachineRecord record);
}

