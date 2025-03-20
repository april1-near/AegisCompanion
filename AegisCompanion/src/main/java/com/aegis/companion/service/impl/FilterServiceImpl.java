package com.aegis.companion.service.impl;

import com.aegis.companion.service.FilterService;
import com.aegis.companion.dao.UserMapper;
import com.aegis.companion.model.entity.User;
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
