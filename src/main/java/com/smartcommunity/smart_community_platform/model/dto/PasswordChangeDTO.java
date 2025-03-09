package com.smartcommunity.smart_community_platform.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "密码修改请求参数")
public class PasswordChangeDTO {
    @Schema(description = "旧密码", required = true)
    private String oldPassword;

    @Schema(description = "新密码（需满足复杂度要求）", required = true)
    private String newPassword;
}