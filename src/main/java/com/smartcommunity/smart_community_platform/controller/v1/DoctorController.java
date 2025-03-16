package com.smartcommunity.smart_community_platform.controller.v1;

import com.smartcommunity.smart_community_platform.model.dto.ScheduleUpdateDTO;
import com.smartcommunity.smart_community_platform.model.vo.DoctorVO;
import com.smartcommunity.smart_community_platform.model.vo.ResponseResult;
import com.smartcommunity.smart_community_platform.service.DoctorService;
import com.smartcommunity.smart_community_platform.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 医生信息管理控制器
 * <p>提供医生基础信息查询接口</p>
 */
@RestController
@RequestMapping("/api/v1/doctors")
@Tag(name = "医生信息管理", description = "医生基本信息查询接口")
@RequiredArgsConstructor
public class DoctorController {


    private final DoctorService doctorService;
    private final ScheduleService scheduleService;

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

}
