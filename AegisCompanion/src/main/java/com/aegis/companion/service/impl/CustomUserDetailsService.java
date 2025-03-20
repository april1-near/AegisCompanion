package com.aegis.companion.service.impl;

import com.aegis.companion.security.CustomUserDetails;
import com.aegis.companion.dao.UserMapper;
import com.aegis.companion.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 从数据库查询用户

        User user = userMapper.findByUsername(username);

        if (user==null) throw new UsernameNotFoundException("用户不存在"+username);

        // 2. 封装为UserDetails对象
        return new CustomUserDetails(user);
    }


}
