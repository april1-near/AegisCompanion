package com.aegis.companion.service;

import com.aegis.companion.model.dto.AppointmentRequestDTO;
import com.aegis.companion.model.vo.AppointmentVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AppointmentService {
    @Transactional(rollbackFor = Exception.class)
    AppointmentVO createAppointment(AppointmentRequestDTO dto, Long userId);

    @Transactional(rollbackFor = Exception.class)
    AppointmentVO cancelAppointment(Long appointmentId, Long userId);

    List<AppointmentVO> listUserAppointments(Long userId);

    @Transactional(rollbackFor = Exception.class)
    void confirmCompletion(Long appointmentId, Long doctorId);
}
