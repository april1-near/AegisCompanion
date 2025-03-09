package com.smartcommunity.smart_community_platform.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.smart_community_platform.dao.CommunityRoomMapper;
import com.smartcommunity.smart_community_platform.dao.RoomBookingMapper;
import com.smartcommunity.smart_community_platform.dao.UserMapper;
import com.smartcommunity.smart_community_platform.exception.BusinessException;
import com.smartcommunity.smart_community_platform.model.dto.BookingQueryDTO;
import com.smartcommunity.smart_community_platform.model.entity.CommunityRoom;
import com.smartcommunity.smart_community_platform.model.entity.RoomBooking;
import com.smartcommunity.smart_community_platform.model.entity.User;
import com.smartcommunity.smart_community_platform.model.enums.BookingStatusEnum;
import com.smartcommunity.smart_community_platform.model.vo.BookingRecordAdminVO;
import com.smartcommunity.smart_community_platform.service.CommunityRoomAdminService;
import com.smartcommunity.smart_community_platform.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityRoomAdminServiceImpl implements CommunityRoomAdminService {

    private final CommunityRoomMapper communityRoomMapper;
    private final RoomBookingMapper roomBookingMapper;
    private final RolePermissionService rolePermissionService;
    private final UserMapper userMapper;

    /**
     * 创建活动室
     *
     * @param room  活动室数据
     * @param admin 操作管理员
     * @return 创建后的活动室ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRoom(CommunityRoom room, User admin) {
        rolePermissionService.checkAdminPermission(admin);
        validateRoomUnique(room);

        communityRoomMapper.insert(room);
        return room.getId();
    }

    /**
     * 更新活动室信息
     *
     * @param room  更新数据（需包含ID）
     * @param admin 操作管理员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoom(CommunityRoom room, User admin) {
        rolePermissionService.checkAdminPermission(admin);
        CommunityRoom existing = getExistingRoom(room.getId());

        if (!existing.getRoomName().equals(room.getRoomName())) {
            validateRoomUnique(room);
        }

        communityRoomMapper.updateById(room);
    }

    /**
     * 删除活动室
     *
     * @param roomId 活动室ID
     * @param admin  操作管理员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoom(Long roomId, User admin) {
        rolePermissionService.checkAdminPermission(admin);
        CommunityRoom room = getExistingRoom(roomId);

        if (roomBookingMapper.hasFutureBookings(roomId)) {
            throw new BusinessException("存在未来预约，无法删除");
        }

        communityRoomMapper.deleteById(roomId);
    }

    /**
     * 分页查询审批记录
     *
     * @param query 查询条件
     * @param page  分页参数
     * @param admin 操作管理员
     * @return 分页审批记录
     */
    @Override
    public IPage<BookingRecordAdminVO> queryBookingRecords(BookingQueryDTO query, Page<RoomBooking> page, User admin) {
        rolePermissionService.checkAdminPermission(admin);

        return roomBookingMapper.selectAdminBookings(page, query).convert(this::buildBookingVO);
    }

    /**
     * 停用活动室
     *
     * @param roomId 活动室ID
     * @param admin  操作管理员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deactivateRoom(Long roomId, User admin) {
        rolePermissionService.checkAdminPermission(admin);
        CommunityRoom room = getExistingRoom(roomId);

        if (!room.getIsActive()) {
            throw new BusinessException("活动室已处于停用状态");
        }

        // 停用并取消未来预约
        communityRoomMapper.updateStatus(roomId, false);
        roomBookingMapper.cancelFutureBookings(roomId);
    }

    /**
     * 启用活动室
     *
     * @param roomId 活动室ID
     * @param admin  操作管理员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateRoom(Long roomId, User admin) {
        rolePermissionService.checkAdminPermission(admin);
        CommunityRoom room = getExistingRoom(roomId);

        if (room.getIsActive()) {
            throw new BusinessException("活动室已处于启用状态");
        }

        communityRoomMapper.updateStatus(roomId, true);
    }

    // 以下为内部辅助方法

    private void validateRoomUnique(CommunityRoom room) {
        if (communityRoomMapper.existsByName(room.getRoomName())) {
            throw new BusinessException("活动室名称已存在");
        }
    }

    private CommunityRoom getExistingRoom(Long roomId) {
        return Optional.ofNullable(communityRoomMapper.selectById(roomId))
                .orElseThrow(() -> new BusinessException("活动室不存在"));
    }

    private BookingRecordAdminVO buildBookingVO(RoomBooking booking) {
        CommunityRoom room = communityRoomMapper.selectById(booking.getRoomId());
        User user = userMapper.selectById(booking.getUserId());
        String description = Objects.requireNonNull(BookingStatusEnum.getByCode(booking.getBookingStatus())).getDescription();
        return BookingRecordAdminVO.builder()
                .id(booking.getId())
                .roomName(room.getRoomName())
                .userName(user.getUsername())
                .purpose(booking.getPurpose())
                .participantCount(booking.getParticipantCount())
                .bookingStatus(description)
                .timeRange(formatTimeRange(booking.getStartTime(), booking.getEndTime()))
                .build();
    }

    private String formatTimeRange(LocalDateTime start, LocalDateTime end) {
        return start.toLocalDate() + " " + start.toLocalTime() + " ~ " + end.toLocalTime();
    }

}
