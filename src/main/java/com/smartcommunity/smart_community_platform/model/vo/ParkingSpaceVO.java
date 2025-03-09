package com.smartcommunity.smart_community_platform.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartcommunity.smart_community_platform.model.entity.ParkingSpace;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 车位列表项VO
// 文件：ParkingSpaceVO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpaceVO {
    private Long id;
    private String zoneCode;       // 区域编码
    private String displayNumber;  // 显示编号（如B2-023）
    private String statusDesc;     // 状态描述
    private String qrCodeUrl;      // 二维码地址
    @JsonFormat(pattern = "MM-dd HH:mm")
    private LocalDateTime lastStatusTime; // 最后状态变更时间

    public static ParkingSpaceVO fromEntity(ParkingSpace entity) {
        return new ParkingSpaceVO(
                entity.getId(),
                entity.getZoneCode(),
                entity.getZoneCode() + "-" + entity.getNumber(),
                entity.getStatus().getDescription(),
                entity.getQrCode(),
                entity.getLastStatusTime()
        );
    }
}

