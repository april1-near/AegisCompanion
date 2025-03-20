package com.aegis.companion.service;

import com.aegis.companion.model.dto.ScheduleUpdateDTO;
import com.aegis.companion.model.vo.ScheduleVO;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {

    List<ScheduleVO> getFutureSchedules(Long doctorId);

    List<ScheduleVO> getDoctorSchedule(Long doctorId, LocalDate startDate, LocalDate endDate);

    @Transactional(rollbackFor = Exception.class)
    void updateSchedule(ScheduleUpdateDTO dto);
}
