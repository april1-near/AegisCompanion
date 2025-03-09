package com.smartcommunity.smart_community_platform.service;

import com.smartcommunity.smart_community_platform.model.dto.ScheduleUpdateDTO;
import com.smartcommunity.smart_community_platform.model.vo.ScheduleVO;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    List<ScheduleVO> getFutureSchedules(Long doctorId);

    List<ScheduleVO> getDoctorSchedule(Long doctorId, LocalDate startDate, LocalDate endDate);

    @Transactional(rollbackFor = Exception.class)
    void updateSchedule(ScheduleUpdateDTO dto);
}
