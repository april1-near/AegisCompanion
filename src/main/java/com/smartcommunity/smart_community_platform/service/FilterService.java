package com.smartcommunity.smart_community_platform.service;

import com.smartcommunity.smart_community_platform.model.entity.User;
import org.springframework.transaction.annotation.Transactional;

public interface FilterService {
    //jwtAuthFilter内部查询方法
    @Transactional(readOnly = true)
    User loadUserWithValidation(String username, Long userId);
}
