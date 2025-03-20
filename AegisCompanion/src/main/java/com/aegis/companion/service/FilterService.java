package com.aegis.companion.service;

import com.aegis.companion.model.entity.User;
import org.springframework.transaction.annotation.Transactional;

public interface FilterService {
    //jwtAuthFilter内部查询方法
    @Transactional(readOnly = true)
    User loadUserWithValidation(String username, Long userId);
}
