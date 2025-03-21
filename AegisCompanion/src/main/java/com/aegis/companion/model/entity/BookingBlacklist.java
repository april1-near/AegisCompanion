package com.aegis.companion.model.entity;

import com.aegis.companion.model.enums.BlacklistReasonEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.EnumTypeHandler;

import java.time.LocalDateTime;

// 黑名单实体
@Data
@TableName("booking_blacklist")
public class BookingBlacklist {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    @TableField(typeHandler = EnumTypeHandler.class)
    private BlacklistReasonEnum reasonType;  // 对应枚举类型
    private LocalDateTime expireTime;
}
