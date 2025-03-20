package com.aegis.companion.service.impl;

import com.aegis.companion.exception.BusinessException;
import com.aegis.companion.service.RolePermissionService;
import com.aegis.companion.service.RoomAdminService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aegis.companion.dao.CommunityRoomMapper;
import com.aegis.companion.dao.RoomBookingMapper;
import com.aegis.companion.dao.UserMapper;
import com.aegis.companion.model.dto.BookingQueryDTO;
import com.aegis.companion.model.dto.RoomCreateDTO;
import com.aegis.companion.model.dto.RoomUpdateDTO;
import com.aegis.companion.model.entity.CommunityRoom;
import com.aegis.companion.model.entity.RoomBooking;
import com.aegis.companion.model.entity.User;
import com.aegis.companion.model.enums.BookingStatusEnum;
import com.aegis.companion.model.vo.BookingRecordAdminVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomAdminServiceImpl implements RoomAdminService {

    private final CommunityRoomMapper communityRoomMapper;
    private final RoomBookingMapper roomBookingMapper;
    private final RolePermissionService rolePermissionService;
    private final UserMapper userMapper;


    @Override
    public List<CommunityRoom> listAllRooms(User admin) {
        rolePermissionService.checkAdminPermission(admin);
        return communityRoomMapper.selectList(
                new QueryWrapper<CommunityRoom>().orderByDesc("create_time"));
    }


    /**
     * 创建活动室
     *
     * @param dto  活动室数据
     * @param admin 操作管理员
     * @return 创建后的活动室ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRoom(RoomCreateDTO dto, User admin) {
        rolePermissionService.checkAdminPermission(admin);
        validateRoomUnique(dto.getRoomName());
        CommunityRoom room = new CommunityRoom()
                .setRoomName(dto.getRoomName())
                .setRoomType(dto.getRoomType())
                .setMaxCapacity(dto.getMaxCapacity())
                .setOpenHour(dto.getOpenHour())
                .setCloseHour(dto.getCloseHour())
                .setIsActive(true)  // 新建默认激活
                .setCreateTime(LocalDateTime.now());
        communityRoomMapper.insert(room);
        return room.getId();
    }

    /**
     * 更新活动室信息
     *
     * @param dto  更新数据（需包含ID）
     * @param admin 操作管理员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoom(RoomUpdateDTO dto, User admin) {
        rolePermissionService.checkAdminPermission(admin);
        CommunityRoom existing = getExistingRoom(dto.getId());
        if (!existing.getRoomName().equals(dto.getRoomName())) {
            validateRoomUnique(dto.getRoomName());
        }
        CommunityRoom updateEntity = new CommunityRoom()
                .setId(dto.getId())
                .setRoomName(dto.getRoomName())
                .setRoomType(dto.getRoomType())
                .setMaxCapacity(dto.getMaxCapacity())
                .setOpenHour(dto.getOpenHour())
                .setCloseHour(dto.getCloseHour())
                .setIsActive(dto.getIsActive());
        communityRoomMapper.updateById(updateEntity);
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

        if(query.getStatus().isEmpty())query.setStatus(null);
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

    private void validateRoomUnique(String roomName) {
        if (communityRoomMapper.existsByName(roomName)) {
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
