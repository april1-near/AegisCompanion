package com.aegis.companion.service;

import com.aegis.companion.model.dto.BookingQueryDTO;
import com.aegis.companion.model.dto.RoomCreateDTO;
import com.aegis.companion.model.dto.RoomUpdateDTO;
import com.aegis.companion.model.entity.CommunityRoom;
import com.aegis.companion.model.entity.RoomBooking;
import com.aegis.companion.model.entity.User;
import com.aegis.companion.model.vo.BookingRecordAdminVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoomAdminService {
    List<CommunityRoom> listAllRooms(User admin);

    @Transactional(rollbackFor = Exception.class)
    Long createRoom(RoomCreateDTO dto, User admin);

    @Transactional(rollbackFor = Exception.class)
    void updateRoom(RoomUpdateDTO dto, User admin);

    @Transactional(rollbackFor = Exception.class)
    void deleteRoom(Long roomId, User admin);

    IPage<BookingRecordAdminVO> queryBookingRecords(BookingQueryDTO query, Page<RoomBooking> page, User admin);

    @Transactional(rollbackFor = Exception.class)
    void deactivateRoom(Long roomId, User admin);

    @Transactional(rollbackFor = Exception.class)
    void activateRoom(Long roomId, User admin);
}
