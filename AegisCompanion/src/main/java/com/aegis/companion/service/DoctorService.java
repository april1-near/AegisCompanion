package com.aegis.companion.service;

import com.aegis.companion.model.vo.DoctorVO;

import java.util.List;

public interface DoctorService {
    List<DoctorVO> listAllDoctors();

    DoctorVO selectDoctorById(Long doctorId);
}
