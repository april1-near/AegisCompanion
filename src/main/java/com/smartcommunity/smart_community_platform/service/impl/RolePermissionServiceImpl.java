package com.smartcommunity.smart_community_platform.service.impl; // 根据你的实际包路径调整

import com.smartcommunity.smart_community_platform.exception.BusinessException;
import com.smartcommunity.smart_community_platform.model.entity.ParkingReservation;
import com.smartcommunity.smart_community_platform.model.entity.User;
import com.smartcommunity.smart_community_platform.model.enums.ReservationStatus;
import com.smartcommunity.smart_community_platform.model.enums.Role;
import com.smartcommunity.smart_community_platform.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
    private static final Long SYSTEM_ADMIN_ID = 1L;

    @Override
    public boolean hasRole(User user, String roleCode) {
        return user.getRole().getCode().equalsIgnoreCase(roleCode);
    }

    @Override
    public void checkViewPermission(User currentUser, Long targetUserId) {
        if (currentUser.isAdmin()) return;
        if (!currentUser.getId().equals(targetUserId)) {
            throw new AccessDeniedException("无权查看他人信息");
        }
    }

    @Override
    public void checkAdminPermission(User currentUser) {
        if (!currentUser.isAdmin()) {
            throw new AccessDeniedException("无权查看用户表信息");
        }
    }

    @Override
    public void checkUpdatePermission(User currentUser, Long targetUserId) {
        // 关键修复：处理未认证用户场景
        if (currentUser == null) {
            throw new AccessDeniedException("User 不能为空");
        }
        if (currentUser.isAdmin()) return;
        if (!currentUser.getId().equals(targetUserId)) {
            throw new AccessDeniedException("无权修改他人信息");
        }
    }

    @Override
    public void checkAssignPermission(User operator, Role targetRole) {
        if (!operator.isAdmin()) {
            throw new AccessDeniedException("无权分配角色");
        }
        if (targetRole == Role.SUPER_ADMIN) {
            throw new AccessDeniedException("禁止分配系统管理员角色");
        }
    }

    @Override
    public void checkDeletePermission(Long targetUserId) {
        if (SYSTEM_ADMIN_ID.equals(targetUserId)) {
            throw new BusinessException("系统管理员账户受保护");
        }
    }


    @Override
    public void checkAssignPermission(User operator, User worker) {
        if (!operator.isAdmin()) {
            throw new AccessDeniedException("无权进行工单分配");
        }
        if (worker.getRole() != Role.MAINTENANCE) {
            throw new AccessDeniedException("目标用户不是维修人员");
        }
    }


    @Override
    public void checkStatusChangePermission(User operator, String targetStatus) {
        if (operator.isAdmin()) return;

        if (targetStatus.equals("processing") && operator.getRole() != Role.MAINTENANCE) {
            throw new AccessDeniedException("仅维修工可接单");
        }
    }


    /**
     * 校验用户是否具有车位预约权限
     *
     * @param user 当前操作用户
     * @throws AccessDeniedException 当用户无权限时抛出
     */
    @Override
    public void checkReservationPermission(User user) {
        // 基础权限校验：用户必须启用且非管理员角色
        if (!user.isEnabled()) {
            throw new AccessDeniedException("账户已被禁用，无法预约车位");
        }
        if (user.getRole() != Role.USER) {
            throw new AccessDeniedException("仅居民用户可预约车位");
        }
    }

    /**
     * 校验用户是否具有查看车位预约记录的权限
     *
     * @param currentUser  当前操作用户
     * @param targetUserId 目标用户ID
     */
    @Override
    public void checkReservationViewPermission(User currentUser, Long targetUserId) {
        // 管理员可查看所有记录
        if (currentUser.isAdmin()) return;

        // 普通用户只能查看自己的记录
        if (!currentUser.getId().equals(targetUserId)) {
            throw new AccessDeniedException("无权查看他人预约记录");
        }
    }

    /**
     * 校验车位管理操作权限（管理员专属）
     *
     * @param operator 操作发起人
     */
    @Override
    public void checkParkingManagePermission(User operator) {
        if (!operator.isAdmin()) {
            throw new AccessDeniedException("需要管理员权限");
        }

    }


    // 新增权限校验方法

    /**
     * 校验用户是否有操作指定预约的权限
     *
     * @param currentUser 当前用户
     * @param reservation 预约记录
     * @throws AccessDeniedException 无权限时抛出
     */
    @Override
    public void checkReservationOperationPermission(User currentUser, ParkingReservation reservation) {
        // 管理员无需校验
        if (currentUser.isAdmin()) return;

        // 用户必须与预约记录所有者一致
        if (!currentUser.getId().equals(reservation.getUserId())) {
            throw new AccessDeniedException("无权操作他人预约");
        }

        // 检查预约是否可操作状态
        if (!ReservationStatus.ACTIVE.getCode().equals(reservation.getStatus())) {
            throw new BusinessException("该预约不可操作");
        }
    }

    // 新增权限校验方法

    /**
     * 校验车位离开操作权限
     *
     * @param user        当前用户
     * @param reservation 预约记录
     */
    @Override
    public void checkLeavePermission(User user, ParkingReservation reservation) {
        // 管理员无需校验
        if (user.isAdmin()) return;

        // 用户必须与预约所有者一致
        if (!user.getId().equals(reservation.getUserId())) {
            throw new AccessDeniedException("无权操作他人预约");
        }

        // 检查车位当前状态
        if (reservation.getStatus().equals(ReservationStatus.COMPLETED.getCode())) {
            throw new BusinessException("该预约已完成");
        }
    }


    @Override
    public void checkCancelBookingPermission(User currentUser, Long bookingId) {
        // 管理员无需校验
        if (currentUser.isAdmin()) return;

        // 用户必须与预约记录所有者一致
        if (!currentUser.getId().equals(bookingId)) {
            throw new AccessDeniedException("无权操作他人预约");
        }

    }


}
