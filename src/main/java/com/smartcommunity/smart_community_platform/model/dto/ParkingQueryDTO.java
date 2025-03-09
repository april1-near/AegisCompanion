package com.smartcommunity.smart_community_platform.model.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

// 车位查询参数
@Data
public class ParkingQueryDTO {
    @NotBlank(message = "区域编码不能为空")
    @Pattern(regexp = "^[A-Z]$", message = "区域编码必须是单个大写字母")
    private String zoneCode;

    @AssertTrue(message = "无效的区域编码")
    public boolean isValidZone() {
        // 实际校验逻辑可扩展
        return zoneCode != null && zoneCode.matches("^[A-F]$");
    }
}
