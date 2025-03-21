package com.aegis.companion.service.impl;

import com.aegis.companion.dao.DoctorMapper;
import com.aegis.companion.model.entity.Doctor;
import com.aegis.companion.model.vo.DoctorVO;
import com.aegis.companion.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorMapper doctorMapper;

    /**
     * 获取所有医生列表
     *
     * @return 医生视图对象列表
     */
    @Override
    public List<DoctorVO> listAllDoctors() {
        return doctorMapper.selectList(null).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorVO selectDoctorById(Long doctorId) {

        return convertToVO(doctorMapper.selectById(doctorId));

    }

    /**
     * 实体转视图对象
     *
     * @param entity 医生实体
     * @return 医生视图对象
     */
    private DoctorVO convertToVO(Doctor entity) {
        return new DoctorVO()
                .setId(entity.getId())
                .setName(entity.getName())
                .setTitle(entity.getTitle())
                .setIntroduction(entity.getIntroduction());
    }
}
