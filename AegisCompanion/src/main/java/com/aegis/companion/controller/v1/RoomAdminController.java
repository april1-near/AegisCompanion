package com.aegis.companion.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aegis.companion.model.dto.BookingApproveDTO;
import com.aegis.companion.model.dto.BookingQueryDTO;
import com.aegis.companion.model.dto.RoomCreateDTO;
import com.aegis.companion.model.dto.RoomUpdateDTO;
import com.aegis.companion.model.entity.CommunityRoom;
import com.aegis.companion.model.vo.BookingRecordAdminVO;
import com.aegis.companion.model.vo.ResponseResult;
import com.aegis.companion.security.CustomUserDetails;
import com.aegis.companion.service.RoomAdminService;
import com.aegis.companion.service.RoomBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社区活动室管理控制器
 */
@RestController
@RequestMapping("/api/v1/admin/rooms")
@RequiredArgsConstructor
@Tag(name = "活动室管理", description = "管理员专用活动室管理接口")
public class RoomAdminController {

    private final RoomAdminService adminService;
    private final RoomBookingService roomBookingService;



    // 新增端点
    /**
     * 查询所有活动室
     */
    @GetMapping
    @Operation(summary = "查询所有活动室",
            description = "获取全部活动室列表（按创建时间倒序）")
    public ResponseResult<List<CommunityRoom>> listAllRooms(
            @AuthenticationPrincipal CustomUserDetails adminDetails) {
        return ResponseResult.success(
                adminService.listAllRooms(adminDetails.user())
        );
    }



    /**
     * 创建活动室
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "创建活动室")
    public ResponseResult<Long> createRoom(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @RequestBody @Validated RoomCreateDTO dto) {
        return ResponseResult.success(
                adminService.createRoom(dto, adminDetails.user())
        );
    }


    /**
     * 更新活动室信息
     */
    @PutMapping("/{roomId}")
    @Operation(summary = "更新活动室")
    public ResponseResult<?> updateRoom(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable Long roomId,
            @RequestBody @Validated RoomUpdateDTO dto) {
        dto.setId(roomId); // 绑定路径参数
        adminService.updateRoom(dto, adminDetails.user());
        return ResponseResult.success("更新成功");
    }


    /**
     * 删除活动室
     */
    @DeleteMapping("/{roomId}")
    @Operation(summary = "删除活动室", description = "删除前自动检查未来预约")
    public ResponseResult<?> deleteRoom(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable Long roomId) {
        adminService.deleteRoom(roomId, adminDetails.user());
        return ResponseResult.success("删除成功");
    }

    /**
     * 分页查询审批记录
     */
    @PostMapping("/bookings")
    @Operation(summary = "审批记录查询", description = "支持状态/活动室/时间范围过滤")
    public ResponseResult<IPage<BookingRecordAdminVO>> queryBookings(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @RequestBody BookingQueryDTO dto,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseResult.success(
                adminService.queryBookingRecords(dto,
                        new Page<>(current, size),
                        adminDetails.user()
                )
        );
    }

    /**
     * 审批预约申请
     *
     * @param dto         审批参数
     * @param userDetails 管理员用户
     * @return 操作结果
     */
    @PutMapping("/approval")
    @Operation(summary = "审批申请",
            description = "管理员审批预约申请，需管理员权限")
    public ResponseResult<?> approveBooking(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Validated BookingApproveDTO dto) {
        roomBookingService.approveBooking(dto.getBookingId(), dto, userDetails.user());
        return ResponseResult.success("审批操作完成");
    }


    /**
     * 停用活动室
     */
    @PostMapping("/{roomId}/deactivate")
    @Operation(summary = "停用活动室", description = "停用后自动取消未来预约")
    public ResponseResult<?> deactivateRoom(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable Long roomId) {
        adminService.deactivateRoom(roomId, adminDetails.user());
        return ResponseResult.success("已停用");
    }

    /**
     * 启用活动室
     */
    @PostMapping("/{roomId}/activate")
    @Operation(summary = "启用活动室", description = "恢复活动室可用状态")
    public ResponseResult<?> activateRoom(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable Long roomId) {
        adminService.activateRoom(roomId, adminDetails.user());
        return ResponseResult.success("已启用");
    }
}
