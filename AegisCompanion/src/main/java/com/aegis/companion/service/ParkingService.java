package com.aegis.companion.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aegis.companion.model.dto.ParkingSpaceCreateDTO;
import com.aegis.companion.model.dto.ParkingSpaceUpdateDTO;
import com.aegis.companion.model.entity.ParkingSpace;
import com.aegis.companion.model.vo.ParkingSpaceVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ParkingService {

    List<ParkingSpaceVO> getAvailableSpaces(
            String zoneCode);

    // 新增车位（带基础校验）
    @Transactional
    Long addParkingSpace(ParkingSpaceCreateDTO dto);

    // 分页查询（带区域过滤）
    IPage<ParkingSpace> listByPage(int pageNum, int pageSize, String zoneCode);

    // 更新车位状态（带乐观锁）
    @Transactional
    void updateParkingSpace(ParkingSpaceUpdateDTO dto);

    // 删除车位（带关联校验）
    @Transactional
    void deleteParkingSpace(Long id);

    // 快速全量列表（调试用）
    List<ParkingSpace> listAll();
}
