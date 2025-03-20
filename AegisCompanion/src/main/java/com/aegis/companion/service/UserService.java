package com.aegis.companion.service;

import com.aegis.companion.model.dto.UserLoginDTO;
import com.aegis.companion.model.dto.UserRegisterDTO;
import com.aegis.companion.model.dto.UserUpdateDTO;
import com.aegis.companion.model.enums.Role;
import com.aegis.companion.model.vo.PageResult;
import com.aegis.companion.model.vo.UserVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    UserVO register(UserRegisterDTO dto);

    String login(UserLoginDTO dto);

    UserVO getUserById(Long id);

    void deleteUser(Long id);

    @Transactional
    void updateUserInfo(Long userId, UserUpdateDTO dto);

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    String resetPassword(Long userId);

    void changePassword(Long id, String oldPassword, String newPassword);

    void updateUserRole(Long id, Role newRole);

    void setUserStatus(Long id, Boolean enabled);

    PageResult<UserVO> listUsers(Integer page, Integer size);

}