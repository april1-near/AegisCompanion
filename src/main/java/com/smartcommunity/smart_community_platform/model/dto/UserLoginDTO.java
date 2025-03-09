package com.smartcommunity.smart_community_platform.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDTO {
    @NotBlank(message = "登录名不能为空")
    private String username;
    @NotBlank(message = "登录密码不能为空")
    private String password;
}