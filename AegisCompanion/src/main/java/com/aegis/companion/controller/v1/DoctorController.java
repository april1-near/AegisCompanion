package com.aegis.companion.controller.v1;

import com.aegis.companion.model.dto.ScheduleUpdateDTO;
import com.aegis.companion.model.vo.DoctorVO;
import com.aegis.companion.model.vo.ResponseResult;
import com.aegis.companion.model.vo.ScheduleVO;
import com.aegis.companion.service.DoctorService;
import com.aegis.companion.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生信息管理控制器
 * <p>提供医生基础信息查询接口</p>
 */
@RestController
@RequestMapping("/api/v1/admin/doctors")
@Tag(name = "医生信息管理", description = "医生基本信息查询接口")
@RequiredArgsConstructor
public class DoctorController {


    private final DoctorService doctorService;
    private final ScheduleService scheduleService;


    @GetMapping()
    @Operation(summary = "查询医生列表", description = "获取所有在岗医生基本信息")
    public ResponseResult<List<DoctorVO>> listAllDoctors() {
        return ResponseResult.success(doctorService.listAllDoctors());
    }


    /**
     * 获取医生详细信息
     *
     * @param doctorId 医生ID
     * @return 医生详细信息
     */
    @GetMapping("/{doctorId}")
    @Operation(summary = "查询医生详情", description = "获取指定医生的详细信息")
    public ResponseResult<DoctorVO> getDoctorDetail(@PathVariable Long doctorId) {
        return ResponseResult.success(doctorService.selectDoctorById(doctorId));
    }

    @PutMapping("/schedules")
    @Operation(summary = "修改医生排班")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseResult<?> updateSchedule(
            @RequestBody @Validated ScheduleUpdateDTO dto) {
        scheduleService.updateSchedule(dto);
        return ResponseResult.success("排班更新成功");
    }


    /**
     * 查询医生排班表
     *
     * @param doctorId  医生ID
     * @return 排班信息列表
     */
    @GetMapping("/schedules/{doctorId}")
    @Operation(summary = "查询医生排班", description = "获取未来日期的医生排班表")
    public ResponseResult<List<ScheduleVO>> getDoctorSchedule(
            @PathVariable Long doctorId){
        return ResponseResult.success(
                scheduleService.getFutureSchedules(doctorId)
        );
    }

}
