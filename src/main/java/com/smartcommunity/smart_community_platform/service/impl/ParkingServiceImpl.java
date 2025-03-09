package com.smartcommunity.smart_community_platform.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.smart_community_platform.dao.ParkingSpaceMapper;
import com.smartcommunity.smart_community_platform.exception.BusinessException;
import com.smartcommunity.smart_community_platform.model.entity.ParkingSpace;
import com.smartcommunity.smart_community_platform.model.enums.ParkingSpaceStatus;
import com.smartcommunity.smart_community_platform.model.vo.ParkingSpaceVO;
import com.smartcommunity.smart_community_platform.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingServiceImpl implements ParkingService {
    private final ParkingSpaceMapper parkingSpaceMapper;

    @Override
    public List<ParkingSpaceVO> getAvailableSpaces(String zoneCode) {
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


    // 新增车位（带基础校验）
    @Override
    @Transactional
    public Long addParkingSpace(ParkingSpace space) {
        // 校验区域编码格式
        if (!isValidZoneCode(space.getZoneCode())) {
            throw new BusinessException("区域编码必须为1位大写字母");
        }

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

    // 更新车位状态（带乐观锁）
    @Override
    @Transactional
    public void updateParkingSpace(ParkingSpace space) {
        if (parkingSpaceMapper.updateById(space) == 0) {
            throw new BusinessException("车位信息已变更，请刷新后重试");
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