package com.aegis.companion.controller.v1;

import com.aegis.companion.model.dto.ReviewHandleDTO;
import com.aegis.companion.model.dto.TicketCreateDTO;
import com.aegis.companion.model.vo.ResponseResult;
import com.aegis.companion.model.vo.TicketVO;
import com.aegis.companion.security.CustomUserDetails;
import com.aegis.companion.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工单管理控制器
 *
 * <p>提供工单创建、状态变更、查询等核心功能接口</p>
 *
 * @author yourname
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Tag(name = "智能工单管理", description = "包含工单创建、状态跟踪、审核处理等操作")
public class TicketController {

    private final TicketService ticketService;

    /**
     * 创建新工单
     *
     * @param dto         工单创建参数
     * @param userDetails 当前登录用户信息
     * @return 空响应体（成功状态码200表示创建成功）
     * @apiNote 创建成功后自动触发工单分配流程
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "创建新工单", description = "居民用户创建新的服务工单")
    public ResponseResult<String> createTicket(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid TicketCreateDTO dto) {
        ticketService.createTicket(dto, userDetails.user().getId());
        return ResponseResult.success("200");
    }


    /**
     * 开始处理工单
     *
     * @param ticketId    工单ID
     * @param userDetails 当前登录维修人员
     * @return 空响应体
     */
    @PostMapping("/{ticketId}/start-processing")
    @PreAuthorize("hasRole('MAINTENANCE')")
    @Operation(summary = "开始处理工单", description = "维修人员开始处理已分配的工单")
    public ResponseResult<Void> startProcessing(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ticketService.startProcessing(ticketId, userDetails.user().getId());
        return ResponseResult.success();
    }


    /**
     * 获取当前用户工单列表
     *
     * @param userDetails 当前登录用户信息
     * @return 工单视图对象列表（按创建时间倒序排列）
     */
    @GetMapping("/my")
    @Operation(summary = "获取我的工单", description = "获取当前用户发起的所有工单记录")
    public ResponseResult<List<TicketVO>> getMyTickets(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseResult.success(
                ticketService.getUserTickets(userDetails.user().getId())
        );
    }

    /**
     * 获取待处理工单列表（维修人员）
     *
     * @param userDetails 当前登录维修人员信息
     * @return 工单视图对象列表（按分配时间正序排列）
     */
    @GetMapping("/assigned")
    @PreAuthorize("hasRole('MAINTENANCE')")
    @Operation(summary = "获取待处理工单", description = "维修人员获取已分配处理的工单列表")
    public ResponseResult<List<TicketVO>> getAssignedTickets(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseResult.success(
                ticketService.getWorkerTickets(userDetails.user().getId())
        );
    }

    /**
     * 提交工单审核
     *
     * @param ticketId    工单ID
     * @param userDetails 当前登录维修人员信息
     * @return 空响应体（成功状态码200表示操作成功）
     */
    @PostMapping("/{ticketId}/submit-review")
    @PreAuthorize("hasRole('MAINTENANCE')")
    @Operation(summary = "提交审核", description = "维修人员完成处理后提交工单审核")
    public ResponseResult<Void> submitForReview(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ticketService.submitForReview(ticketId, userDetails.user().getId());
        return ResponseResult.success();
    }

    /**
     * 处理工单审核
     *
     * @param ticketId    工单ID
     * @param dto 审批说明
     * @param userDetails 当前登录管理员信息
     * @return 空响应体（成功状态码200表示操作成功）
     */
    @PostMapping("/{ticketId}/handle-review")
    @PreAuthorize("hasAnyRole('ADMIN','SYSTEM_ADMIN')")
    @Operation(summary = "处理审核", description = "管理人员处理工单审核（通过/驳回）")
    public ResponseResult<Void> handleReview(
            @PathVariable Long ticketId,
            @Valid @RequestBody ReviewHandleDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ticketService.handleReview(ticketId, dto, userDetails.user().getId());
        return ResponseResult.success();
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取所有工单", description = "管理员查看所有工单，按创建时间倒序排列")
    public ResponseResult<List<TicketVO>> getAllTickets() {
        return ResponseResult.success(ticketService.getAllTickets());
    }

    /**
     * 确认工单完成
     *
     * @param ticketId    工单ID
     * @param userDetails 当前登录用户信息
     * @return 空响应体（成功状态码200表示操作成功）
     */
    @PostMapping("/{ticketId}/confirm")
    @Operation(summary = "确认完成", description = "用户确认工单已完成服务")
    public ResponseResult<Void> confirmCompletion(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ticketService.confirmCompletion(ticketId, userDetails.user().getId());
        return ResponseResult.success();
    }


}
