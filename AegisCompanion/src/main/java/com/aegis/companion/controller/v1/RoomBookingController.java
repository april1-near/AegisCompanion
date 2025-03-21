package com.aegis.companion.controller.v1;

import com.aegis.companion.model.dto.BookingCreateDTO;
import com.aegis.companion.model.entity.CommunityRoom;
import com.aegis.companion.model.entity.RoomBooking;
import com.aegis.companion.model.vo.BookingRecordVO;
import com.aegis.companion.model.vo.ResponseResult;
import com.aegis.companion.security.CustomUserDetails;
import com.aegis.companion.service.RoomBookingService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社区活动室预约管理控制器
 *
 * <p>提供活动室预约申请、取消、审批及查询功能</p>
 */
@RestController
@RequestMapping("/api/v1/room-bookings")
@RequiredArgsConstructor
@Tag(name = "活动室预约管理", description = "包含活动室预约申请、审批等操作")
public class RoomBookingController {

    private final RoomBookingService roomBookingService;

    /**
     * 获取当前可用活动室列表
     *
     * @return 可用活动室列表
     */
    @GetMapping("/available-rooms")
    @Operation(summary = "查询可用活动室",
            description = "查询当前可预约的活动室列表")
    public ResponseResult<List<CommunityRoom>> getAvailableRooms() {

        return ResponseResult.success(
                roomBookingService.getAvailableRooms()
        );
    }


    /**
     * 创建活动室预约申请
     *
     * @param dto         预约请求参数
     * @param userDetails 当前登录用户
     * @return 创建成功的预约记录
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "创建预约申请",
            description = "居民用户提交活动室使用申请，需校验时间冲突和用户权限")
    public ResponseResult<BookingRecordVO> createBooking(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Validated BookingCreateDTO dto) {
        return ResponseResult.success(
                roomBookingService.createBooking(dto, userDetails.user())
        );
    }

    /**
     * 取消预约申请
     *
     * @param bookingId   预约记录ID
     * @param userDetails 当前登录用户
     * @return 操作结果
     */
    @DeleteMapping("/{bookingId}")
    @Operation(summary = "取消预约",
            description = "用户取消待审批状态的预约申请")
    public ResponseResult<?> cancelBooking(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long bookingId) {
        roomBookingService.cancelBooking(bookingId, userDetails.user());
        return ResponseResult.success("预约已取消");
    }


    /**
     * 分页查询用户预约记录
     *
     * @param userDetails 当前登录用户
     * @param current     起始页参数
     * @param size        条目数量
     * @return 分页的预约记录
     */

    @GetMapping
    @Operation(summary = "查询预约记录",
            description = "查询当前用户的预约历史记录")
    public ResponseResult<Page<BookingRecordVO>> queryUserBookings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        Page<RoomBooking> page = new Page<>(current, size);
        return ResponseResult.success(
                (Page<BookingRecordVO>) roomBookingService.queryUserBookings(userDetails.user(), page)
        );
    }
}
