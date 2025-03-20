package com.aegis.companion.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

// 预约记录实体
@Data
@Accessors(chain = true)
@TableName("parking_reservation")
public class ParkingReservation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long spaceId;
    @JsonFormat(pattern = "MM-dd HH:mm")
    private LocalDateTime reserveTime;
    @JsonFormat(pattern = "MM-dd HH:mm")
    private LocalDateTime expireTime;
    @JsonFormat(pattern = "MM-dd HH:mm")
    private LocalDateTime actualUseTime;
    private String status;
    @Version
    private Integer version; // 乐观锁版本号
}
