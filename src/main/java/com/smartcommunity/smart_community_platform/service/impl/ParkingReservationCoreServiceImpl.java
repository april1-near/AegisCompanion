package com.smartcommunity.smart_community_platform.service.impl;
// 新增预约核心业务逻辑类

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.smart_community_platform.dao.ParkingLogMapper;
import com.smartcommunity.smart_community_platform.dao.ParkingReservationMapper;
import com.smartcommunity.smart_community_platform.dao.ParkingSpaceMapper;
import com.smartcommunity.smart_community_platform.exception.BusinessException;
import com.smartcommunity.smart_community_platform.model.dto.ParkingOperationDetail;
import com.smartcommunity.smart_community_platform.model.dto.ReservationCreateDTO;
import com.smartcommunity.smart_community_platform.model.entity.ParkingLog;
import com.smartcommunity.smart_community_platform.model.entity.ParkingReservation;
import com.smartcommunity.smart_community_platform.model.entity.ParkingSpace;
import com.smartcommunity.smart_community_platform.model.entity.User;
import com.smartcommunity.smart_community_platform.model.enums.ParkingEventType;
import com.smartcommunity.smart_community_platform.model.enums.ParkingSpaceStatus;
import com.smartcommunity.smart_community_platform.model.enums.ReservationStatus;
import com.smartcommunity.smart_community_platform.service.ParkingReservationCoreService;
import com.smartcommunity.smart_community_platform.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * 车位预约核心服务类
 * 处理预约创建、状态变更等核心业务流程
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ParkingReservationCoreServiceImpl implements ParkingReservationCoreService {
    private final RolePermissionService rolePermissionService;
    private final ParkingSpaceMapper parkingSpaceMapper;
    private final ParkingReservationMapper reservationMapper;
    private final ParkingLogMapper logMapper;

    /**
     * 创建车位预约（完整事务逻辑）
     *
     * @param dto  预约请求参数
     * @param user 当前用户
     * @return 预约记录ID
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ParkingReservation createReservation(ReservationCreateDTO dto, User user) {
        rolePermissionService.checkReservationPermission(user);

        ParkingSpace space = parkingSpaceMapper.selectById(dto.getSpaceId());
        if (space.getStatus() != ParkingSpaceStatus.FREE) {
            throw new BusinessException("该车位当前不可预约");
        }

        ParkingReservation reservation = new ParkingReservation()
                .setUserId(user.getId())
                .setSpaceId(dto.getSpaceId())
                .setReserveTime(LocalDateTime.now())
                .setExpireTime(LocalDateTime.now().plusMinutes(30))
                .setStatus(ReservationStatus.ACTIVE.getCode());
        reservationMapper.insert(reservation);

        updateSpaceStatusWithLock(space, ParkingSpaceStatus.RESERVED);

        logMapper.insert(buildLog(dto.getSpaceId(), user,
                ParkingEventType.RESERVE,
                ParkingSpaceStatus.FREE,
                ParkingSpaceStatus.RESERVED,
                "用户预约成功"));

        return reservation;

    }


// 新增方法：获取当前用户有效预约

    /**
     * 获取用户当前有效预约
     *
     * @param user 当前用户
     * @return 有效预约记录（最多一条）
     */

    @Override
    public List<ParkingReservation> listUserReservations(User user) {
        // 查询用户所有历史预约记录（按时间倒序）
        return reservationMapper.selectList(new LambdaQueryWrapper<ParkingReservation>()
                .eq(ParkingReservation::getUserId, user.getId())
                .orderByDesc(ParkingReservation::getReserveTime));
    }


// 新增方法：取消预约

    /**
     * 取消车位预约（带事务管理）
     *
     * @param reservationId 预约记录ID
     * @param user          当前用户
     * @return
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ParkingReservation cancelReservation(Long reservationId, User user) {
        ParkingReservation reservation = getValidReservation(reservationId);
        rolePermissionService.checkReservationOperationPermission(user, reservation);

        reservation.setStatus(ReservationStatus.CANCELED.getCode());
        reservationMapper.updateById(reservation);

        ParkingSpace space = parkingSpaceMapper.selectById(reservation.getSpaceId());
        updateSpaceStatusWithLock(space, ParkingSpaceStatus.FREE);

        logMapper.insert(buildLog(reservation.getSpaceId(), user,
                ParkingEventType.RELEASE,
                ParkingSpaceStatus.RESERVED,
                ParkingSpaceStatus.FREE,
                "用户取消预约"));

        return reservation;
    }


// 新增方法：确认车辆到达

    /**
     * 确认车辆到达车位（带事务管理）
     *
     * @param reservationId 预约记录ID
     * @param user          当前用户
     * @return
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ParkingReservation confirmOccupancy(Long reservationId, User user) {
        ParkingReservation reservation = getValidReservation(reservationId);
        rolePermissionService.checkReservationOperationPermission(user, reservation);

        reservation.setStatus(ReservationStatus.ARRIVED.getCode())
                .setActualUseTime(LocalDateTime.now());
        reservationMapper.updateById(reservation);

        ParkingSpace space = parkingSpaceMapper.selectById(reservation.getSpaceId());
        updateSpaceStatusWithLock(space, ParkingSpaceStatus.OCCUPIED);

        logMapper.insert(buildLog(reservation.getSpaceId(), user,
                ParkingEventType.OCCUPY,
                ParkingSpaceStatus.RESERVED,
                ParkingSpaceStatus.OCCUPIED,
                "用户确认到达"));

        return reservation;
    }


    // 新增方法：用户离开释放车位
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ParkingReservation leaveParkingSpace(Long reservationId, User user) {
        ParkingReservation reservation = getValidReservation(reservationId);
        rolePermissionService.checkLeavePermission(user, reservation);

        // 更新预约状态
        reservation.setStatus(ReservationStatus.COMPLETED.getCode());
        reservationMapper.updateById(reservation);

        // 释放车位
        ParkingSpace space = parkingSpaceMapper.selectById(reservation.getSpaceId());
        updateSpaceStatusWithLock(space, ParkingSpaceStatus.FREE);

        // 记录日志
        logMapper.insert(buildLog(reservation.getSpaceId(), user,
                ParkingEventType.RELEASE,
                ParkingSpaceStatus.OCCUPIED,
                ParkingSpaceStatus.FREE,
                "用户离开车位"));
        return reservation;
    }

    /**
     * 自动释放超时未确认的预约（每小时执行）
     * 分页处理避免大数据量压力
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Scheduled(cron = "0 0 * * * *") // 每小时整点执行
    public void autoReleaseExpiredReservations() {
        int pageSize = 20; // 每页处理量（根据DB性能调整）
        int pageNum = 1;
        boolean hasNext = true;

        while (hasNext) {
            // 分页查询需要放在事务内
            hasNext = processExpiredBatch(pageNum, pageSize);
            pageNum++;
        }
    }

    /**
     * 处理单批次过期预约
     *
     * @return 是否还有后续数据
     */
    public boolean processExpiredBatch(int pageNum, int pageSize) {
        // 1. 分页查询过期预约
        Page<ParkingReservation> page = new Page<>(pageNum, pageSize);
        IPage<ParkingReservation> expiredPage = reservationMapper.selectExpiredReservationsPage(
                page,
                LocalDateTime.now()
        );

        // 2. 处理当前页数据
        expiredPage.getRecords().forEach(reservation -> {
            // 2.1 更新预约状态
            reservation.setStatus(ReservationStatus.EXPIRED.getCode());
            reservationMapper.updateById(reservation);

            // 2.2 释放车位
            ParkingSpace space = parkingSpaceMapper.selectById(reservation.getSpaceId());
            updateSpaceStatusWithLock(space, ParkingSpaceStatus.FREE);

            // 2.3 记录系统日志
            logMapper.insert(buildLog(reservation.getSpaceId(), null,
                    ParkingEventType.TIMEOUT_RELEASE,
                    ParkingSpaceStatus.RESERVED,
                    ParkingSpaceStatus.FREE,
                    "系统自动释放超时预约"));
        });

        // 3. 判断是否还有下一页
        return expiredPage.getRecords().size() >= pageSize;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ParkingReservation adminForceRelease(Long spaceId, User admin) {
        // 1. 管理员权限校验
        rolePermissionService.checkParkingManagePermission(admin);

        // 2. 获取目标车位
        ParkingSpace space = parkingSpaceMapper.selectById(spaceId);
        if (space.getStatus() == ParkingSpaceStatus.FREE) {
            throw new BusinessException("车位当前无需释放");
        }

        // 3. 查找关联预约记录
        ParkingReservation reservation = reservationMapper.selectActiveBySpace(spaceId);

        // 4. 更新预约状态（如果存在）
        if (reservation != null) {
            reservation.setStatus(ReservationStatus.CANCELED.getCode());
            reservationMapper.updateById(reservation);
        }

        updateSpaceStatusWithLock(space, ParkingSpaceStatus.FREE);

        // 6. 记录管理员操作日志
        logMapper.insert(buildLog(spaceId, admin,
                ParkingEventType.ADMIN_RELEASE,
                space.getStatus(),  // 原状态
                ParkingSpaceStatus.FREE,
                "管理员强制释放"));

        return reservation;
    }


    // 其他辅助方法...
    // 核心辅助方法：带乐观锁更新车位状态
    private void updateSpaceStatusWithLock(ParkingSpace space, ParkingSpaceStatus newStatus) {
        space.updateStatus(newStatus);
        if (parkingSpaceMapper.updateById(space) == 0) {
            throw new ConcurrentModificationException("车位状态变更冲突");
        }
    }

    // 统一日志构建方法
    private ParkingLog buildLog(Long spaceId, User user,
                                ParkingEventType operationType,
                                ParkingSpaceStatus oldStatus,
                                ParkingSpaceStatus newStatus,
                                String remark) {
        long id = 1L;
        if (user != null) {
            id = user.getId();
        }
        return new ParkingLog()
                .setSpaceId(spaceId)
                .setOperatorId(id)
                .setOperationType(operationType)
                .setDetail(new ParkingOperationDetail()
                        .setOldStatus(oldStatus.name())
                        .setNewStatus(newStatus.name())
                        .setRemark(remark))
                .setLogTime(LocalDateTime.now());
    }

    // 基础校验方法
    private ParkingReservation getValidReservation(Long reservationId) {
        ParkingReservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预约记录不存在");
        }
        return reservation;
    }
}