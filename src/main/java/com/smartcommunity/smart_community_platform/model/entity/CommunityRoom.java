package com.smartcommunity.smart_community_platform.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartcommunity.smart_community_platform.model.enums.RoomTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.EnumTypeHandler;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Accessors(chain = true)
@TableName("community_room")
public class CommunityRoom {
    private Long id;
    private String roomName;
    @TableField(typeHandler = EnumTypeHandler.class)
    private RoomTypeEnum roomType;
    private Integer maxCapacity;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openHour;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeHour;
    private Boolean isActive;
    @JsonFormat(pattern = "YY:MM:dd HH:mm")
    private LocalDateTime createTime;

}
