package com.aegis.companion.model.entity;

import com.aegis.companion.model.enums.ParkingSpaceStatus;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.EnumTypeHandler;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("parking_space")
public class ParkingSpace {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String zoneCode;   // 区域编码
    private String number;     // 车位编号
    @TableField(typeHandler = EnumTypeHandler.class)
    private ParkingSpaceStatus status;     // 状态枚举值
    private String qrCode;     // 二维码地址
    @JsonFormat(pattern = "YY:MM:dd HH:mm")
    private LocalDateTime lastStatusTime;
    @JsonFormat(pattern = "YY:MM:dd HH:mm")
    private LocalDateTime createTime;
    @Version
    private Integer version; // 乐观锁版本号

    public ParkingSpace updateStatus(ParkingSpaceStatus newStatus) {
        this.status = newStatus;
        this.lastStatusTime = LocalDateTime.now();
        return this;
    }
}