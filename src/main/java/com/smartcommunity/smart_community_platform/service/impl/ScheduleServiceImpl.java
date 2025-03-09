package com.smartcommunity.smart_community_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.smart_community_platform.dao.ScheduleMapper;
import com.smartcommunity.smart_community_platform.model.dto.ScheduleUpdateDTO;
import com.smartcommunity.smart_community_platform.model.entity.Schedule;
import com.smartcommunity.smart_community_platform.model.enums.TimeSlotStatusEnum;
import com.smartcommunity.smart_community_platform.model.vo.ScheduleVO;
import com.smartcommunity.smart_community_platform.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleMapper scheduleMapper;



    /**
     * 获取医生未来全部排班（无需日期范围）
     * @param doctorId 医生ID
     * @return 未来所有排班视图对象列表（按日期升序排列）
     */
    @Override
    public List<ScheduleVO> getFutureSchedules(Long doctorId) {
        LambdaQueryWrapper<Schedule> query = new LambdaQueryWrapper<Schedule>()
                .eq(Schedule::getDoctorId, doctorId)
                .ge(Schedule::getWorkDate, LocalDate.now()) // 只查未来日期
                .orderByAsc(Schedule::getWorkDate);

        return scheduleMapper.selectList(query).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }



    /**
     * 获取医生排班表
     *
     * @param doctorId  医生ID
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return 排班视图对象列表
     */
    @Override
    public List<ScheduleVO> getDoctorSchedule(Long doctorId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Schedule> query = new LambdaQueryWrapper<Schedule>()
                .eq(Schedule::getDoctorId, doctorId)
                .between(Schedule::getWorkDate, startDate, endDate);

        return scheduleMapper.selectList(query).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 更新排班表
     *
     * @param dto 排班更新参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSchedule(ScheduleUpdateDTO dto) {
        // 获取排班记录时初始化空数据
        Schedule schedule = scheduleMapper.selectByDoctorAndDate(
                dto.getDoctorId(), dto.getWorkDate());

        if (schedule == null) {
            schedule = new Schedule()
                    .setDoctorId(dto.getDoctorId())
                    .setWorkDate(dto.getWorkDate())
                    .setTimeSlots(new HashMap<>(dto.getTimeSlots())); // 确保不为null
            scheduleMapper.insert(schedule);
        } else {
            // 确保原始数据不为null
            if (schedule.getTimeSlots() == null) {
                schedule.setTimeSlots(new HashMap<>());
            }

            Map<String, TimeSlotStatusEnum> merged = mergeTimeSlots(
                    schedule.getTimeSlots(),
                    dto.getTimeSlots()
            );
            schedule.setTimeSlots(merged);
            scheduleMapper.updateById(schedule);
        }
    }

    /**
     * 合并时间段状态
     *
     * @param original 原始时间段
     * @param updates  更新内容
     * @return 合并后的时间段
     */
    private Map<String, TimeSlotStatusEnum> mergeTimeSlots(
            Map<String, TimeSlotStatusEnum> original,
            Map<String, TimeSlotStatusEnum> updates) {

        // 防御性拷贝（允许original为null）
        Map<String, TimeSlotStatusEnum> merged = original != null ?
                new HashMap<>(original) : new HashMap<>();

        // 直接覆盖式更新（无需条件判断）
        if (updates != null) {
            merged.putAll(updates);
        }

        return merged;
    }

    /**
     * 实体转视图对象
     *
     * @param entity 排班实体
     * @return 排班视图对象
     */
    private ScheduleVO convertToVO(Schedule entity) {
        return new ScheduleVO()
                .setWorkDate(entity.getWorkDate())
                .setTimeSlots(entity.getTimeSlots());
    }
}
