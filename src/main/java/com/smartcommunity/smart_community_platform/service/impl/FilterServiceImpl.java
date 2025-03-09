package com.smartcommunity.smart_community_platform.service.impl;

import com.smartcommunity.smart_community_platform.dao.UserMapper;
import com.smartcommunity.smart_community_platform.model.entity.User;
import com.smartcommunity.smart_community_platform.service.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {

    private final UserMapper userMapper;


    //jwtAuthFilter内部查询方法
    @Override
    @Transactional(readOnly = true)
    public User loadUserWithValidation(String username, Long userId) {
        return userMapper.selectByUsernameAndId(username, userId);
    }

}
