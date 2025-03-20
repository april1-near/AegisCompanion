package com.aegis.companion.service.impl;

import com.aegis.companion.exception.BusinessException;
import com.aegis.companion.service.RolePermissionService;
import com.aegis.companion.service.RoomBookingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aegis.companion.dao.BookingBlacklistMapper;
import com.aegis.companion.dao.CommunityRoomMapper;
import com.aegis.companion.dao.RoomBookingMapper;
import com.aegis.companion.model.dto.BookingApproveDTO;
import com.aegis.companion.model.dto.BookingCreateDTO;
import com.aegis.companion.model.entity.CommunityRoom;
import com.aegis.companion.model.entity.RoomBooking;
import com.aegis.companion.model.entity.User;
import com.aegis.companion.model.enums.BookingStatusEnum;
import com.aegis.companion.model.vo.BookingRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RoomBookingServiceImpl extends ServiceImpl<RoomBookingMapper, RoomBooking> implements RoomBookingService {

    private final RolePermissionService rolePermissionService;
    private final CommunityRoomMapper communityRoomMapper;
    private final BookingBlacklistMapper bookingBlacklistMapper;

    /**
     * 创建活动室预约申请
     *
     * @param dto 预约请求参数
     * @param currentUser 当前登录用户
     * @return 创建成功预约记录视图
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingRecordVO createBooking(BookingCreateDTO dto, User currentUser) {
        // 1. 校验用户预约权限
        rolePermissionService.checkReservationPermission(currentUser);

        // 2. 校验黑名单状态
        checkUserBlacklist(currentUser.getId());

        // 3. 获取并验证活动室可用性
        CommunityRoom room = validateRoomAvailability(dto.getRoomId());

        // 4. 校验预约时间有效性
        validateBookingTime(dto.getStartTime(), dto.getEndTime(), room);

        // 5. 校验参与人数不超过容量限制
        validateParticipantCount(dto.getParticipantCount(), room.getMaxCapacity());

        // 6. 检测时间冲突
        checkTimeConflict(dto.getRoomId(), dto.getStartTime(), dto.getEndTime());

        // 7. 构建并保存预约记录
        RoomBooking booking = buildBookingEntity(dto, currentUser.getId());
        baseMapper.insert(booking);

        return convertToVO(booking);
    }

    /**
     * 查询当前时段可用活动室
     * @return 可用活动室列表
     */
    @Override
    public List<CommunityRoom> getAvailableRooms() {
        return communityRoomMapper.selectAvailableRooms();
    }


    /**
     * 取消预约申请
     *
     * @param bookingId   预约记录ID
     * @param currentUser 当前登录用户
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingRecordVO cancelBooking(Long bookingId, User currentUser) {
        RoomBooking booking = getValidBooking(bookingId);

        // 校验操作权限：用户只能取消自己的预约
        rolePermissionService.checkCancelBookingPermission(currentUser, booking.getUserId());

        if (!BookingStatusEnum.PENDING.getCode().equals(booking.getBookingStatus())) {
            throw new BusinessException("仅可取消待审批状态的预约");
        }

        booking.setBookingStatus(BookingStatusEnum.CANCELED.getCode());
        baseMapper.updateById(booking);
        return convertToVO(booking);
    }

    /**
     * 审批预约申请
     *
     * @param bookingId 预约记录ID
     * @param dto       审批参数
     * @param admin     审批管理员
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingRecordVO approveBooking(Long bookingId, BookingApproveDTO dto, User admin) {
        // 校验管理员权限
        rolePermissionService.checkAdminPermission(admin);

        RoomBooking booking = getValidBooking(bookingId);
        CommunityRoom room = communityRoomMapper.selectById(booking.getRoomId());

        if (dto.getApproved()) {  // 使用正确的字段名

            checkTimeConflict(booking.getRoomId(), booking.getStartTime(), booking.getEndTime());
            booking.setBookingStatus(BookingStatusEnum.APPROVED.getCode());
        } else {
            booking.setBookingStatus(BookingStatusEnum.REJECTED.getCode())
                    .setAdminComment(dto.getComment());
        }
        baseMapper.updateById(booking);
        return convertToVO(booking);
    }

    /**
     * 分页查询用户预约记录
     *
     * @param currentUser 当前登录用户
     * @param page        分页参数
     * @return 分页的预约记录视图
     */
    @Override
    public IPage<BookingRecordVO> queryUserBookings(User currentUser, Page<RoomBooking> page) {


        LambdaQueryWrapper<RoomBooking> queryWrapper = new LambdaQueryWrapper<RoomBooking>()
                .eq(RoomBooking::getUserId, currentUser.getId())
                .orderByDesc(RoomBooking::getStartTime);

        IPage<RoomBooking> bookingPage = baseMapper.selectPage(page, queryWrapper);
        return bookingPage.convert(this::convertToVO);
    }

    // 以下是内部辅助方法

    /**
     * 验证活动室可用性
     *
     * @param roomId 活动室ID
     * @return 有效的活动室实体
     */
    private CommunityRoom validateRoomAvailability(Long roomId) {
        CommunityRoom room = communityRoomMapper.selectById(roomId);
        if (room == null || !room.getIsActive()) {
            throw new BusinessException("活动室不可用");
        }
        return room;
    }

    /**
     * 校验预约时间有效性
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param room  活动室实体
     */
    private void validateBookingTime(LocalDateTime start, LocalDateTime end, CommunityRoom room) {
        LocalTime startLocalTime = start.toLocalTime();
        LocalTime endLocalTime = end.toLocalTime();

        if (startLocalTime.isBefore(room.getOpenHour()) || endLocalTime.isAfter(room.getCloseHour())) {
            throw new BusinessException("预约时间超出开放时段");
        }
    }

    /**
     * 检查用户黑名单状态
     *
     * @param userId 用户ID
     */
    private void checkUserBlacklist(Long userId) {
        if (bookingBlacklistMapper.existsValidRecord(userId, LocalDateTime.now())) {
            throw new BusinessException("您已被限制使用预约功能");
        }
    }

    /**
     * 检测时间冲突
     *
     * @param roomId 活动室ID
     * @param start  开始时间
     * @param end    结束时间
     */
    private void checkTimeConflict(Long roomId, LocalDateTime start, LocalDateTime end) {
        Integer conflictCount = baseMapper.countTimeConflict(roomId, start, end);
        if (conflictCount > 1) {
            throw new BusinessException("存在时间冲突的预约");
        }
    }

    /**
     * 构建预约实体
     *
     * @param dto    创建参数
     * @param userId 用户ID
     * @return 预约实体
     */
    private RoomBooking buildBookingEntity(BookingCreateDTO dto, Long userId) {
        return new RoomBooking()
                .setUserId(userId)
                .setRoomId(dto.getRoomId())
                .setPurpose(dto.getPurpose())
                .setParticipantCount(dto.getParticipantCount())
                .setStartTime(dto.getStartTime())
                .setEndTime(dto.getEndTime())
                .setBookingStatus(BookingStatusEnum.PENDING.getCode())
                .setVersion(0);
    }

    /**
     * 转换视图对象
     *
     * @param booking 预约实体
     * @return 预约记录视图
     */
    private BookingRecordVO convertToVO(RoomBooking booking) {

        return BookingRecordVO.builder()
                .userId(booking.getUserId())
                .id(booking.getId())
                .purpose(booking.getPurpose())
                .timeRange(formatTimeRange(booking.getStartTime(), booking.getEndTime()))
                .status(BookingStatusEnum.getByCode(booking.getBookingStatus()).getDescription())
                .statusStyle(getStatusStyleClass(booking.getBookingStatus()))
                .rejectReason(booking.getAdminComment())
                .build();
    }


    /**
     * 获取有效的预约记录
     *
     * @param bookingId 预约ID
     * @return 有效的预约实体
     */
    private RoomBooking getValidBooking(Long bookingId) {
        RoomBooking booking = baseMapper.selectById(bookingId);
        if (booking == null) {
            throw new BusinessException("预约记录不存在");
        }
        return booking;
    }

    /**
     * 获取关联活动室信息
     *
     * @param roomId 活动室ID
     * @return 活动室实体
     */
    private CommunityRoom getRelatedRoom(Long roomId) {
        return communityRoomMapper.selectById(roomId);
    }

    /**
     * 格式化时间范围
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 格式化后的字符串
     */
    private String formatTimeRange(LocalDateTime start, LocalDateTime end) {
        return start.toLocalDate() + " " + start.toLocalTime() + " ~ " + end.toLocalTime();
    }

    /**
     * 获取状态对应的样式类
     *
     * @param statusCode 状态编码
     * @return CSS样式类名
     */
    private String getStatusStyleClass(String statusCode) {
        return Objects.requireNonNull(BookingStatusEnum.getByCode(statusCode)).getStyleClass();
    }

    /**
     * 校验参与人数限制
     *
     * @param participants 参与人数
     * @param maxCapacity  最大容量
     */
    private void validateParticipantCount(Integer participants, Integer maxCapacity) {
        if (participants > maxCapacity) {
            throw new BusinessException("参与人数超过最大容量");
        }
    }
}
