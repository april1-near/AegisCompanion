package com.aegis.companion.controller.v1;

import com.aegis.companion.model.dto.AppointmentRequestDTO;
import com.aegis.companion.model.vo.AppointmentVO;
import com.aegis.companion.model.vo.DoctorVO;
import com.aegis.companion.model.vo.ResponseResult;
import com.aegis.companion.model.vo.ScheduleVO;
import com.aegis.companion.security.CustomUserDetails;
import com.aegis.companion.service.AppointmentService;
import com.aegis.companion.service.DoctorService;
import com.aegis.companion.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医疗预约管理控制器
 * <p>提供医生信息查询、预约创建、取消及记录查询功能</p>
 */
@RestController
@RequestMapping("/api/v1/Medical")
@RequiredArgsConstructor
@Tag(name = "医疗预约管理", description = "包含医生排班查询、预约申请等操作")
public class MedicalController {

    private final DoctorService doctorService;
    private final ScheduleService scheduleService;
    private final AppointmentService appointmentService;

    /**
     * 获取所有医生列表
     *
     * @return 医生基本信息列表
     */
    @GetMapping("/doctors")
    @Operation(summary = "查询医生列表", description = "获取所有在岗医生基本信息")
    public ResponseResult<List<DoctorVO>> listAllDoctors() {
        return ResponseResult.success(doctorService.listAllDoctors());
    }

    /**
     * 查询医生排班表
     *
     * @param doctorId 医生ID
     * @return 排班信息列表
     */
    @GetMapping("/doctors/{doctorId}/schedules")
    @Operation(summary = "查询医生排班", description = "获取指定日期范围内的医生排班表")
    public ResponseResult<List<ScheduleVO>> getDoctorSchedule(
            @PathVariable Long doctorId) {
        return ResponseResult.success(
                scheduleService.getFutureSchedules(doctorId)
        );
    }

    /**
     * 创建医疗预约
     *
     * @param userDetails 当前登录用户
     * @param dto         预约请求参数
     * @return 创建的预约记录
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "创建预约", description = "提交新的医疗预约申请")
    public ResponseResult<AppointmentVO> createAppointment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Validated AppointmentRequestDTO dto) {
        return ResponseResult.success(
                appointmentService.createAppointment(dto, userDetails.user().getId())
        );
    }

    /**
     * 取消医疗预约
     *
     * @param appointmentId 预约记录ID
     * @param userDetails   当前登录用户
     * @return 操作结果
     */
    @DeleteMapping("/{appointmentId}")
    @Operation(summary = "取消预约", description = "取消未开始的预约记录")
    public ResponseResult<?> cancelAppointment(
            @PathVariable Long appointmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        appointmentService.cancelAppointment(appointmentId, userDetails.user().getId());
        return ResponseResult.success("预约已取消");
    }

    @GetMapping("/my-appointments")
    @Operation(summary = "查询我的全部预约")
    public ResponseResult<List<AppointmentVO>> listMyAppointments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseResult.success(
                appointmentService.listUserAppointments(userDetails.user().getId())
        );
    }

    @PatchMapping("/{appointmentId}/complete")
    @Operation(summary = "确认就诊完成")
    public ResponseResult<?> confirmCompletion(
            @PathVariable Long appointmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        appointmentService.confirmCompletion(appointmentId, userDetails.user().getId());
        return ResponseResult.success("就诊确认完成");
    }

}
