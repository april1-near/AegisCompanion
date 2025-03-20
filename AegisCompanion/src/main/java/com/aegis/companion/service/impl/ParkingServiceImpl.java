package com.aegis.companion.service.impl;


import com.aegis.companion.exception.BusinessException;
import com.aegis.companion.service.ParkingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aegis.companion.dao.ParkingSpaceMapper;
import com.aegis.companion.model.dto.ParkingSpaceCreateDTO;
import com.aegis.companion.model.dto.ParkingSpaceUpdateDTO;
import com.aegis.companion.model.entity.ParkingSpace;
import com.aegis.companion.model.enums.ParkingSpaceStatus;
import com.aegis.companion.model.vo.ParkingSpaceVO;
import com.aegis.companion.utils.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingServiceImpl implements ParkingService {
    @Autowired
    private ParkingSpaceMapper parkingSpaceMapper;

    @Override
//    @Tool(description = "查询空闲的车位")
    public List<ParkingSpaceVO> getAvailableSpaces(
//            @ToolParam(description = "只有四个值：{A,B,C,D}")
            String zoneCode) {
        // 参数有效性校验
        if (!isValidZoneCode(zoneCode)) {
            throw new IllegalArgumentException("无效的区域编码: " + zoneCode);
        }

        // 查询数据库（使用索引优化）
        List<ParkingSpace> entities = parkingSpaceMapper.selectFreeSpacesByZone(zoneCode);

        // 实体转VO（含状态码转换）
        return entities.stream()
                .map(ParkingSpaceVO::fromEntity)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public Long addParkingSpace(ParkingSpaceCreateDTO dto) {
        // 转换DTO到实体
        ParkingSpace space = ParkingSpace.builder()
                .zoneCode(dto.getZoneCode())
                .number(dto.getNumber())
                .status(dto.getStatus() != null ? dto.getStatus() : ParkingSpaceStatus.FREE)
                .qrCode(dto.getQrCode())
                .createTime(LocalDateTime.now())
                .lastStatusTime(LocalDateTime.now())
                .build();
        // 校验车位号重复
        LambdaQueryWrapper<ParkingSpace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkingSpace::getZoneCode, space.getZoneCode())
                .eq(ParkingSpace::getNumber, space.getNumber());
        if (parkingSpaceMapper.exists(wrapper)) {
            throw new BusinessException("该车位已存在");
        }
        parkingSpaceMapper.insert(space);
        return space.getId();
    }


    // 分页查询（带区域过滤）
    @Override
    public IPage<ParkingSpace> listByPage(int pageNum, int pageSize, String zoneCode) {
        Page<ParkingSpace> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ParkingSpace> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(zoneCode)) {
            wrapper.eq(ParkingSpace::getZoneCode, zoneCode.toUpperCase());
        }
        return parkingSpaceMapper.selectPage(page, wrapper.orderByAsc(ParkingSpace::getNumber));
    }

    @Override
    @Transactional
    public void updateParkingSpace(ParkingSpaceUpdateDTO dto) {
        // 转换DTO到实体（自动携带version）
        ParkingSpace entity = new ParkingSpace();
        BeanCopyUtils.copyNonNullProperties(dto, entity);

        // 自动更新状态时间（业务逻辑）
        if (dto.getStatus() != null) {
            entity.setLastStatusTime(LocalDateTime.now());
        }
        // MP会自动添加 version 条件
        int rows = parkingSpaceMapper.updateById(entity);
        if (rows == 0) {
            throw new BusinessException("数据不存在或版本已变更");
        }
    }


    // 删除车位（带关联校验）
    @Override
    @Transactional
    public void deleteParkingSpace(Long id) {
        ParkingSpace space = parkingSpaceMapper.selectById(id);
        if (space == null) {
            throw new BusinessException("车位不存在");
        }

        // 检查是否有未完成的预约
        if (space.getStatus() == ParkingSpaceStatus.RESERVED) {
            throw new BusinessException("存在未完成的预约，无法删除");
        }

        parkingSpaceMapper.deleteById(id);
    }

    // 快速全量列表（调试用）
    @Override
    public List<ParkingSpace> listAll() {
        return parkingSpaceMapper.selectList(null);
    }


    private boolean isValidZoneCode(String code) {
        return code.matches("^[A-Z]$"); // 假设单字母区域编码
    }
}