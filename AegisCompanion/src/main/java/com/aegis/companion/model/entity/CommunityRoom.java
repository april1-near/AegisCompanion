package com.aegis.companion.model.entity;

import com.aegis.companion.model.enums.RoomTypeEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;

}
