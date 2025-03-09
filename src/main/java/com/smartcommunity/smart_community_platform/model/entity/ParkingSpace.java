package com.smartcommunity.smart_community_platform.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.smartcommunity.smart_community_platform.model.enums.ParkingSpaceStatus;
import lombok.Builder;
import lombok.Data;
import org.apache.ibatis.type.EnumTypeHandler;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("parking_space")
public class ParkingSpace {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String zoneCode;   // 区域编码
    private String number;     // 车位编号
    @TableField(typeHandler = EnumTypeHandler.class)
    private ParkingSpaceStatus status;     // 状态枚举值
    private String qrCode;     // 二维码地址
    private LocalDateTime lastStatusTime;
    private LocalDateTime createTime;
    @Version
    private Integer version; // 乐观锁版本号

    public ParkingSpace updateStatus(ParkingSpaceStatus newStatus) {
        this.status = newStatus;
        this.lastStatusTime = LocalDateTime.now();
        return this;
    }
}