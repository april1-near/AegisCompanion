package com.smartcommunity.smart_community_platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.smart_community_platform.model.dto.BookingQueryDTO;
import com.smartcommunity.smart_community_platform.model.entity.CommunityRoom;
import com.smartcommunity.smart_community_platform.model.entity.RoomBooking;
import com.smartcommunity.smart_community_platform.model.entity.User;
import com.smartcommunity.smart_community_platform.model.vo.BookingRecordAdminVO;
import org.springframework.transaction.annotation.Transactional;

public interface CommunityRoomAdminService {
    @Transactional(rollbackFor = Exception.class)
    Long createRoom(CommunityRoom room, User admin);

    @Transactional(rollbackFor = Exception.class)
    void updateRoom(CommunityRoom room, User admin);

    @Transactional(rollbackFor = Exception.class)
    void deleteRoom(Long roomId, User admin);

    IPage<BookingRecordAdminVO> queryBookingRecords(BookingQueryDTO query, Page<RoomBooking> page, User admin);

    @Transactional(rollbackFor = Exception.class)
    void deactivateRoom(Long roomId, User admin);

    @Transactional(rollbackFor = Exception.class)
    void activateRoom(Long roomId, User admin);
}
