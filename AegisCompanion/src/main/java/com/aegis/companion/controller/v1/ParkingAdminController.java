// 文件：ParkingAdminController.java
package com.aegis.companion.controller.v1;

import com.aegis.companion.model.dto.ParkingSpaceCreateDTO;
import com.aegis.companion.model.dto.ParkingSpaceUpdateDTO;
import com.aegis.companion.model.entity.ParkingSpace;
import com.aegis.companion.model.vo.ResponseResult;
import com.aegis.companion.security.CustomUserDetails;
import com.aegis.companion.service.ParkingReservationCoreService;
import com.aegis.companion.service.ParkingService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车位管理控制器（管理员专用）
 * 包含车位信息增删改查等管理功能
 */
@RestController
@RequestMapping("/api/v1/admin/parking")
@RequiredArgsConstructor
@Tag(name = "车位管理接口", description = "管理员对车位的增删改查操作")
public class ParkingAdminController {
    private final ParkingService parkingService;
    private final ParkingReservationCoreService reservationCoreService;

    /**
     * 添加新车位（带格式校验）
     *
     * @param space 车位实体（需包含区域编码、编号等信息）
     * @return 创建成功的车位ID
     */
    @PostMapping("/spaces")
    @Operation(summary = "添加车位", description = "管理员创建新的物理车位记录")
    public ResponseResult<Long> addParkingSpace(
            @RequestBody @Valid ParkingSpaceCreateDTO space) {
        return ResponseResult.success(parkingService.addParkingSpace(space));
    }

    /**
     * 分页查询车位信息
     *
     * @param pageNum  页码（从1开始）
     * @param pageSize 每页数量
     * @param zoneCode 区域编码过滤条件（可选）
     * @return 分页后的车位信息列表
     */
    @GetMapping("/spaces")
    @Operation(summary = "分页查询车位", description = "带区域过滤的分页查询")
    public ResponseResult<IPage<ParkingSpace>> listSpaces(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String zoneCode) {
        return ResponseResult.success(parkingService.listByPage(pageNum, pageSize, zoneCode));
    }

    /**
     * 更新车位信息（带乐观锁校验）
     *
     * @param space 包含最新状态的车位实体
     */
    @PutMapping("/spaces/{id}")
    @Operation(summary = "更新车位信息", description = "修改车位状态或基础信息")
    public ResponseResult<?> updateParkingSpace(
            @PathVariable Long id,
            @RequestBody @Valid ParkingSpaceUpdateDTO space) {
        space.setId(id); // 确保ID一致性
        parkingService.updateParkingSpace(space);
        return ResponseResult.success("更新成功");
    }


    @PostMapping("/release/{spaceId}")
    @Operation(summary = "管理员强制释放车位", description = "强制释放指定车位，无论当前状态")
    public ResponseResult<?> adminForceRelease(
            @AuthenticationPrincipal @NonNull CustomUserDetails userDetails,
            @PathVariable Long spaceId) {
        reservationCoreService.adminForceRelease(spaceId, userDetails.user());
        return ResponseResult.success("释放成功");
    }


    /**
     * 删除车位（带关联校验）
     *
     * @param id 要删除的车位ID
     */
    @DeleteMapping("/spaces/{id}")
    @Operation(summary = "删除车位", description = "物理删除空闲状态的车位")
    public ResponseResult<?> deleteParkingSpace(
            @PathVariable Long id) {
        parkingService.deleteParkingSpace(id);
        return ResponseResult.success("删除成功");
    }

    /**
     * 获取全量车位列表（调试用）
     *
     * @return 所有车位信息的完整列表
     */
    @GetMapping("/spaces/all")
    @Operation(summary = "获取全量列表", description = "调试用接口，返回所有车位数据")
    public ResponseResult<List<ParkingSpace>> listAllSpaces() {
        return ResponseResult.success(parkingService.listAll());
    }
}
