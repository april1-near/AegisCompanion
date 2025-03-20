package com.aegis.companion.controller.v1;

import com.aegis.companion.model.enums.Role;
import com.aegis.companion.model.vo.PageResult;
import com.aegis.companion.model.vo.ResponseResult;
import com.aegis.companion.model.vo.UserVO;
import com.aegis.companion.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "用户管理（管理员）", description = "需要管理员权限的高级用户管理接口")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "获取全部用户列表", description = "分页查询系统中的所有用户")
    public ResponseResult<PageResult<UserVO>> listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseResult.success(userService.listUsers(page, size));
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "修改用户角色", description = "调整用户在系统中的权限级别")
    public ResponseResult<String> updateRole(
            @PathVariable Long id,
            @RequestParam @NotNull Role newRole) {
        userService.updateUserRole(id, newRole);
        return ResponseResult.success("success");
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "重置用户密码", description = "强制生成新密码（管理员特权操作）")
    public ResponseResult<String> resetPassword(@PathVariable Long id) {
        return ResponseResult.success(userService.resetPassword(id));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "用户状态管理", description = "启用/禁用指定账户")
    public ResponseResult<String> toggleUserStatus(
            @PathVariable Long id,
            @RequestParam Boolean enabled) {
        userService.setUserStatus(id, enabled);
        return ResponseResult.success("success");
    }
}