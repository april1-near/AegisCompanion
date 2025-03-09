package com.smartcommunity.smart_community_platform.controller.v1;

import com.smartcommunity.smart_community_platform.model.dto.PasswordChangeDTO;
import com.smartcommunity.smart_community_platform.model.dto.UserUpdateDTO;
import com.smartcommunity.smart_community_platform.model.vo.ResponseResult;
import com.smartcommunity.smart_community_platform.model.vo.UserVO;
import com.smartcommunity.smart_community_platform.security.CustomUserDetails;
import com.smartcommunity.smart_community_platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// controller/v1/UserController.java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('USER','MAINTENANCE') and #id == principal.username)")
    @Operation(summary = "获取用户详情", description = "根据用户ID查询详细信息（自动脱敏）")
    public ResponseResult<UserVO> getUserById(
            @AuthenticationPrincipal CustomUserDetails du) {
        return ResponseResult.success(userService.getUserById(du.user().getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == principal.username)")
    @Operation(summary = "更新用户信息", description = "修改个人信息，管理员可操作任意账户")
    public ResponseResult<String> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDTO dto) {
        userService.updateUserInfo(id, dto);
        return ResponseResult.success("用户信息已修改");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == principal.username)")
    @Operation(summary = "删除用户账户", description = "注销账户操作（系统管理员账号受保护）")
    public ResponseResult<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseResult.success("已删除");
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('USER') and #id == principal.username")
    @Operation(summary = "修改个人密码", description = "用户自主修改登录密码")
    public ResponseResult<String> changePassword(
            @PathVariable Long id,
            @RequestBody @Valid PasswordChangeDTO dto) {
        userService.changePassword(id, dto.getOldPassword(), dto.getNewPassword());
        return ResponseResult.success("密码修改成功,请及时保存");
    }


}
