package com.smartcommunity.smart_community_platform.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName("room_booking")
@Accessors(chain = true)
public class RoomBooking {
    private Long id;
    private Long userId;
    private Long roomId;
    private String purpose;
    private Integer participantCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String bookingStatus;
    private String adminComment;
    private Integer version;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
