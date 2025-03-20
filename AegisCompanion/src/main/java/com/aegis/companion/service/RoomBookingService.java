package com.aegis.companion.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aegis.companion.model.dto.BookingApproveDTO;
import com.aegis.companion.model.dto.BookingCreateDTO;
import com.aegis.companion.model.entity.CommunityRoom;
import com.aegis.companion.model.entity.RoomBooking;
import com.aegis.companion.model.entity.User;
import com.aegis.companion.model.vo.BookingRecordVO;
import org.springframework.transaction.annotation.Transactional;

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