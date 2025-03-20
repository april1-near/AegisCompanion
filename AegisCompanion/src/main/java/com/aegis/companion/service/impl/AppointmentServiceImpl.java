package com.aegis.companion.service.impl;

import com.aegis.companion.exception.BusinessException;
import com.aegis.companion.service.AppointmentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.aegis.companion.dao.AppointmentMapper;
import com.aegis.companion.dao.DoctorMapper;
import com.aegis.companion.dao.ScheduleMapper;
import com.aegis.companion.model.dto.AppointmentRequestDTO;
import com.aegis.companion.model.entity.Appointment;
import com.aegis.companion.model.entity.Doctor;
import com.aegis.companion.model.entity.Schedule;
import com.aegis.companion.model.enums.AppointmentStatusEnum;
import com.aegis.companion.model.enums.TimeSlotStatusEnum;
import com.aegis.companion.model.vo.AppointmentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentMapper appointmentMapper;
    private final ScheduleMapper scheduleMapper;
    private final DoctorMapper doctorMapper;

    /**
     * 创建医疗预约
     *
     * @param dto    预约请求参数
     * @param userId 用户ID
     * @return 预约记录视图对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentVO createAppointment(AppointmentRequestDTO dto, Long userId) {
        // 校验时间有效性
        validateTimeSlot(dto.getTimeSlot());

        // 获取排班记录
        Schedule schedule = scheduleMapper.selectByDoctorAndDate(
                dto.getDoctorId(), dto.getAppointDate());

        if (schedule.getTimeSlots().isEmpty()) {
            throw new BusinessException("该医生当日无排班");
        }

        // 检查时间段可用性
        checkTimeSlotAvailable(schedule, dto.getTimeSlot());

        // 锁定时间段
        updateTimeSlotStatus(schedule, dto.getTimeSlot(), TimeSlotStatusEnum.BOOKED);

        // 创建预约记录
        Appointment appointment = buildAppointmentEntity(dto, userId);

        appointmentMapper.insert(appointment);

        return convertToVO(appointment);
    }

    /**
     * 取消医疗预约
     *
     * @param appointmentId 预约ID
     * @param userId        用户ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentVO cancelAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null)throw new BusinessException("工单不存在");

        // 校验操作权限
        if (!appointment.getUserId().equals(userId)) {
            throw new BusinessException("无权操作他人预约");
        }

        // 恢复时间段
        Schedule schedule = scheduleMapper.selectByDoctorAndDate(
                appointment.getDoctorId(), appointment.getAppointDate());
        updateTimeSlotStatus(schedule, appointment.getTimeSlot(), TimeSlotStatusEnum.AVAILABLE);

        // 更新预约状态
        appointment.setStatus(AppointmentStatusEnum.CANCELED);
        appointmentMapper.updateById(appointment);
        return convertToVO(appointment);
    }

    /**
     * 查询用户全部预约记录
     *
     * @param userId 用户ID
     * @return 预约记录列表
     */
    @Override
    public List<AppointmentVO> listUserAppointments(Long userId) {
        LambdaQueryWrapper<Appointment> query = new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getUserId, userId)
                .orderByDesc(Appointment::getAppointDate);

        return appointmentMapper.selectList(query).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }


    /**
     * 确认预约完成
     *
     * @param appointmentId 预约ID
     * @param doctorId      医生ID
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmCompletion(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);

        // 校验医生权限
        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new BusinessException("无权操作其他医生的预约");
        }

        // 状态校验
        if (appointment.getStatus() != AppointmentStatusEnum.CONFIRMED) {
            throw new BusinessException("仅可确认已到诊的预约");
        }

        // 更新状态
        appointment.setStatus(AppointmentStatusEnum.CONFIRMED);
        appointmentMapper.updateById(appointment);
    }


    /**
     * 校验时间段格式
     *
     * @param timeSlot 时间段字符串
     */
    private void validateTimeSlot(String timeSlot) {
        if (!timeSlot.matches("\\d{2}:\\d{2}-\\d{2}:\\d{2}")) {
            throw new BusinessException("时间段格式错误");
        }
    }

    /**
     * 检查时间段可用性
     *
     * @param schedule 排班记录
     * @param timeSlot 目标时间段
     */
    private void checkTimeSlotAvailable(Schedule schedule, String timeSlot) {
        TimeSlotStatusEnum status = schedule.getTimeSlots().get(timeSlot);

        if (status != TimeSlotStatusEnum.AVAILABLE) {
            throw new BusinessException("该时段不可预约");
        }
    }

    /**
     * 更新排班表时间段状态
     *
     * @param schedule  排班记录
     * @param timeSlot  时间段
     * @param newStatus 新状态
     */
    private void updateTimeSlotStatus(Schedule schedule, String timeSlot, TimeSlotStatusEnum newStatus) {
        schedule.getTimeSlots().put(timeSlot, newStatus);
        scheduleMapper.updateById(schedule);
    }

    /**
     * 构建预约实体
     *
     * @param dto    请求参数
     * @param userId 用户ID
     * @return 预约实体
     */
    private Appointment buildAppointmentEntity(AppointmentRequestDTO dto, Long userId) {
        return new Appointment()
                .setUserId(userId)
                .setDoctorId(dto.getDoctorId())
                .setAppointDate(dto.getAppointDate())
                .setTimeSlot(dto.getTimeSlot())
                .setStatus(AppointmentStatusEnum.PENDING);
    }

    /**
     * 转换视图对象
     *
     * @param entity 预约实体
     * @return 预约视图对象
     */
    private AppointmentVO convertToVO(Appointment entity) {
        Doctor doctor = doctorMapper.selectById(entity.getDoctorId());
        return new AppointmentVO()
                .setId(entity.getId())
                .setUserId(entity.getUserId())
                .setDoctorName(doctor.getName())
                .setAppointDate(entity.getAppointDate())
                .setTimeSlot(entity.getTimeSlot())
                .setStatusDesc(entity.getStatus().getDescription());
    }


}
