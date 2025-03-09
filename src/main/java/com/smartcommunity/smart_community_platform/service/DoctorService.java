package com.smartcommunity.smart_community_platform.service;

import com.smartcommunity.smart_community_platform.model.vo.DoctorVO;

import java.util.List;

public interface DoctorService {
    List<DoctorVO> listAllDoctors();

    DoctorVO selectDoctorById(Long doctorId);
}
