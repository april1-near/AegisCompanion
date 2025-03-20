package com.smartcommunity.smart_community_platform.model.dto;

import com.smartcommunity.smart_community_platform.model.enums.ParkingSpaceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpaceCreateDTO {
    @NotBlank(message = "区域编码不能为空")
    @Pattern(regexp = "^[A-D]$", message = "区域编码必须为A-D的1位大写字母")
    private String zoneCode;
    @NotBlank(message = "车位编号不能为空")
    private String number;
    @Builder.Default
    private ParkingSpaceStatus status = ParkingSpaceStatus.FREE;
    private String qrCode;
}
