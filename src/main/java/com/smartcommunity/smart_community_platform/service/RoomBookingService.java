package com.smartcommunity.smart_community_platform.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.smart_community_platform.model.dto.BookingApproveDTO;
import com.smartcommunity.smart_community_platform.model.dto.BookingCreateDTO;
import com.smartcommunity.smart_community_platform.model.entity.CommunityRoom;
import com.smartcommunity.smart_community_platform.model.entity.RoomBooking;
import com.smartcommunity.smart_community_platform.model.entity.User;
import com.smartcommunity.smart_community_platform.model.vo.BookingRecordVO;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

public interface RoomBookingService {

    @Transactional(rollbackFor = Exception.class)
    BookingRecordVO createBooking(BookingCreateDTO dto, User currentUser);

    List<CommunityRoom> getAvailableRooms( );

    @Transactional(rollbackFor = Exception.class)
    BookingRecordVO cancelBooking(Long bookingId, User currentUser);

    @Transactional(rollbackFor = Exception.class)
    BookingRecordVO approveBooking(Long bookingId, BookingApproveDTO dto, User admin);

    IPage<BookingRecordVO> queryUserBookings(User currentUser, Page<RoomBooking> page);
}