package com.aegis.companion.service.impl;

import com.aegis.companion.exception.BusinessException;
import com.aegis.companion.exception.OptimisticLockException;
import com.aegis.companion.service.RolePermissionService;
import com.aegis.companion.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aegis.companion.dao.UserMapper;
import com.aegis.companion.model.dto.UserLoginDTO;
import com.aegis.companion.model.dto.UserRegisterDTO;
import com.aegis.companion.model.dto.UserUpdateDTO;
import com.aegis.companion.model.entity.User;
import com.aegis.companion.model.enums.Role;
import com.aegis.companion.model.vo.PageResult;
import com.aegis.companion.model.vo.UserVO;
import com.aegis.companion.utils.BeanCopyUtils;
import com.aegis.companion.utils.JwtUtil;
import com.aegis.companion.utils.PageUtils;
import com.aegis.companion.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

// src/main/java/com/smart-community/service/impl/UserServiceImpl.java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RolePermissionService rolePermissionService;
    private final JwtUtil jwtUtil;

    // ------------------------ 核心业务方法 ------------------------
    @Override
    @Transactional
    public UserVO register(UserRegisterDTO dto) {
        validateUniqueUser(dto);

        User user = new User()
                .setUsername(dto.getUsername())
                .setPhone(dto.getPhone())
                .setPassword(passwordEncoder.encode(dto.getPassword()))
                .setRole(Role.USER)
                .setIsEnabled(true);
        userMapper.insert(user);
        return convertToVO(user);
    }

    @Override
    public String login(UserLoginDTO dto) {
        User user = getUserByCredential(dto.getUsername());

        validateLogin(user, dto.getPassword());

        return jwtUtil.generateToken(
                user.getUsername(),
                user.getId(),
                user.getRole()
        );
    }

    @Override
    public UserVO getUserById(Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        rolePermissionService.checkViewPermission(currentUser, id);

        return convertToSafeVO(getExistingUser(id));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        rolePermissionService.checkDeletePermission(id);
        userMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateUserInfo(Long userId, UserUpdateDTO dto) {
        User currentUser = SecurityUtils.getCurrentUser();

        rolePermissionService.checkUpdatePermission(currentUser, userId);

        User user = getExistingUser(userId);
        updateUserDetails(user, dto, currentUser);


        System.out.println(user);

        handleOptimisticLock(user);
    }

    @Transactional
    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User currentUser = SecurityUtils.getCurrentUser();
        rolePermissionService.checkUpdatePermission(currentUser, id); // 复用删除保护逻辑
        User user = getExistingUser(id);
        validateLodPassword(user, oldPassword);                         //重新登录以验证旧密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String resetPassword(Long userId) {
        rolePermissionService.checkDeletePermission(userId); // 复用删除保护逻辑
        User user = getExistingUser(userId);
        String newPwd = generateSecurePassword();
        user.setPassword(passwordEncoder.encode(newPwd));
        userMapper.updateById(user);
        return newPwd;
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, Role newRole) {
        User currentUser = SecurityUtils.getCurrentUser();
        User targetUser = getExistingUser(userId);
        rolePermissionService.checkDeletePermission(userId);
        // 更新角色字段（服务内复用逻辑）
        UserUpdateDTO updateDTO = new UserUpdateDTO().setRole(newRole);
        updateUserDetails(targetUser, updateDTO, currentUser);
        handleOptimisticLock(targetUser);
    }

    @Override
    @Transactional
    public void setUserStatus(Long userId, Boolean enabled) {
        User currentUser = SecurityUtils.getCurrentUser();
        User targetUser = getExistingUser(userId);
        log.info("管理员[{}]修改用户[{}]状态为: {}",
                currentUser.getUsername(),
                targetUser.getUsername(),
                enabled ? "启用" : "禁用"
        );
        // Step1 权限校验链
        rolePermissionService.checkDeletePermission(userId); // 限制系统管理员ID

        // Step2 防止管理员禁用自己
        if (currentUser.getId().equals(userId)) {
            throw new BusinessException("禁止操作自身账户状态");
        }
        // Step3 更新状态
        targetUser.setIsEnabled(enabled);
        handleOptimisticLock(targetUser);

    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<UserVO> listUsers(Integer page, Integer size) {
        // Step 1 - 权限验证（方法级安全）
        User currentUser = SecurityUtils.getCurrentUser();
        rolePermissionService.checkAdminPermission(currentUser);

        // Step 2 - 规范分页参数
        int normalizedPage = PageUtils.normalizePage(page);
        int normalizedSize = PageUtils.normalizeSize(size, 100);

        // Step 3 - 构建分页查询条件
        Page<User> pageParam = new Page<>(normalizedPage, normalizedSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>()
                .orderByDesc("create_time");

        // Step 4 - 执行分页查询
        Page<User> userPage = userMapper.selectPage(pageParam, queryWrapper);

        // Step 5 - 数据转换与脱敏处理
        List<UserVO> voList = userPage.getRecords().stream()
                .map(this::convertToSafeVO)
                .collect(Collectors.toList());

        // Step 6 - 包装返回结果
        return new PageResult<UserVO>()
                .setTotal(userPage.getTotal())
                .setRecords(voList);
    }


    // ------------------------ 工具方法 ------------------------
    private void validateUniqueUser(UserRegisterDTO dto) {
        if (userMapper.exists(new QueryWrapper<User>().eq("username", dto.getUsername()))) {
            throw new BusinessException("用户名已存在");
        }
        if (userMapper.exists(new QueryWrapper<User>().eq("phone", dto.getPhone()))) {
            throw new BusinessException("手机号已注册");
        }
    }

    private void validateLodPassword(User user, String lodPasswd) {
        if (!passwordEncoder.matches(lodPasswd, user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
    }


    private User getUserByCredential(String username) {
        return Optional.ofNullable(
                userMapper.selectOne(new QueryWrapper<User>().eq("username", username))
        ).orElseThrow(() -> new BusinessException("用户不存在"));
    }

    private void validateLogin(User user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException("密码错误--" + user.getUsername());
        }
        if (!user.getIsEnabled()) {
            throw new BusinessException("账户已禁用--" + user.getId());
        }
    }

    private UserVO convertToSafeVO(User user) {
        UserVO vo = convertToVO(user);
        if (!SecurityUtils.currentUserIsAdmin()) {
            SecurityUtils.maskSensitiveInfo(vo);
        }
        return vo;
    }

    private User getExistingUser(Long userId) {
        return Optional.ofNullable(userMapper.selectById(userId))
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    private void updateUserDetails(User user, UserUpdateDTO dto, User currentUser) {
        // 使用工具类复制非空字段
        BeanCopyUtils.copyNonNullProperties(dto, user, "id", "password", "version");

        // 单独处理角色更新
        if (dto.getRole() != null && currentUser.isAdmin()) {
            rolePermissionService.checkAssignPermission(currentUser, dto.getRole());
            user.setRole(dto.getRole());
        }
    }

    private void handleOptimisticLock(User user) {
//        user.setVersion(user.getVersion() + 1); // 假设使用 @Version 注解
        if (userMapper.updateById(user) == 0) {
            throw new OptimisticLockException("数据版本冲突");
        }
    }

    private String generateSecurePassword() {
        return RandomStringUtils.randomAlphanumeric(10) + "!@#$%^&*".charAt(new Random().nextInt(8));
    }

    private UserVO convertToVO(User user) {
        return new UserVO()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setPhone(user.getPhone())
                .setEmail(user.getEmail())
                .setRole(user.getRole())
                .setEnabled(user.getIsEnabled());
//                .setSkills(user.getSkills());
    }
}
