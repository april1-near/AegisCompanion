package com.smartcommunity.smart_community_platform.controller.v1;

import com.smartcommunity.smart_community_platform.model.dto.ReservationCreateDTO;
import com.smartcommunity.smart_community_platform.model.entity.ParkingReservation;
import com.smartcommunity.smart_community_platform.model.vo.ParkingSpaceVO;
import com.smartcommunity.smart_community_platform.model.vo.ResponseResult;
import com.smartcommunity.smart_community_platform.security.CustomUserDetails;
import com.smartcommunity.smart_community_platform.service.ParkingReservationCoreService;
import com.smartcommunity.smart_community_platform.service.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 停车位管理控制器
 *
 * <p>提供车位状态查询、预约等核心功能接口</p>
 *
 * @author yourname
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/parking")
@RequiredArgsConstructor
@Tag(name = "智能停车管理", description = "包含车位预约、状态查询等操作")
public class ParkingController {
    private final ParkingService parkingService;
    private final ParkingReservationCoreService reservationCoreService;

    /**
     * 获取指定区域可用停车位列表
     *
     * @param zone 区域编码（示例：A/B/C）
     * @return 可用车位列表（包含状态描述）
     * @apiNote 此接口响应时间≤200ms（P95）
     */
    @GetMapping("/spaces")
    public ResponseResult<List<ParkingSpaceVO>> getAvailableSpaces(
            @RequestParam @NotBlank String zone) {
        return ResponseResult.success(parkingService.getAvailableSpaces(zone));
    }


    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "创建车位预约",
            description = "居民用户创建新的停车预约，需校验车位可用性和用户权限"
    )
    public ResponseResult<ParkingReservation> createReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Validated ReservationCreateDTO dto) {

        ParkingReservation reservationId = reservationCoreService.createReservation(dto, userDetails.user());
        return ResponseResult.success(reservationId);
    }


    @GetMapping("/reservations/current")
    @Operation(summary = "获取用户所有预约记录", description = "获取登录用户全部历史预约（按时间倒序）")
    public ResponseResult<List<ParkingReservation>> listUserReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ParkingReservation> reservations = reservationCoreService.listUserReservations(userDetails.user());
        return ResponseResult.success(reservations);
    }


    @DeleteMapping("/reservations/{id}")
    @Operation(summary = "取消预约", description = "用户取消未过期的预约，释放车位并生成操作日志")
    public ResponseResult<?> cancelReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        reservationCoreService.cancelReservation(id, userDetails.user());
        return ResponseResult.success("取消成功");
    }

    // 新增接口：确认到达
    @PostMapping("/reservations/{id}/confirm")
    @Operation(summary = "确认到达车位", description = "用户到达车位后进行确认操作，变更车位状态")
    public ResponseResult<?> confirmOccupancy(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        reservationCoreService.confirmOccupancy(id, userDetails.user());
        return ResponseResult.success("确认成功");
    }


    // 新增离开接口
    @PostMapping("/reservations/{id}/leave")
    @Operation(summary = "离开车位", description = "用户结束停车时手动确认离开")
    public ResponseResult<?> leaveParking(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        reservationCoreService.leaveParkingSpace(id, userDetails.user());
        return ResponseResult.success("离开确认成功");
    }


}
