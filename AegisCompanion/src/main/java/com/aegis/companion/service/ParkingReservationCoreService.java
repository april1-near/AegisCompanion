package com.aegis.companion.service;

import com.aegis.companion.model.dto.ReservationCreateDTO;
import com.aegis.companion.model.entity.ParkingReservation;
import com.aegis.companion.model.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ParkingReservationCoreService {
    @Transactional(rollbackFor = Exception.class)
    ParkingReservation createReservation(ReservationCreateDTO dto, User user);

    @Transactional(rollbackFor = Exception.class)
    ParkingReservation cancelReservation(Long reservationId, User user);

    @Transactional(rollbackFor = Exception.class)
    ParkingReservation confirmOccupancy(Long reservationId, User user);

    // 新增方法：用户离开释放车位
    @Transactional(rollbackFor = Exception.class)
    ParkingReservation leaveParkingSpace(Long reservationId, User user);

    /**
     * 管理员释放车位
     *
     * @param spaceId 车位id
     * @param admin   管理员
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    ParkingReservation adminForceRelease(Long spaceId, User admin);

    List<ParkingReservation> listUserReservations(User user);
}
