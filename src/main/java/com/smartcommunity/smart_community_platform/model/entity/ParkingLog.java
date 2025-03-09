// 文件：ParkingLog.java
package com.smartcommunity.smart_community_platform.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smartcommunity.smart_community_platform.model.dto.ParkingOperationDetail;
import com.smartcommunity.smart_community_platform.model.enums.ParkingEventType;
import com.smartcommunity.smart_community_platform.utils.ParkingOperationDetailTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 车位操作日志实体
 */
@Data
@Accessors(chain = true)
@TableName("parking_log")
public class ParkingLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("space_id")
    private Long spaceId;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("operation_type")
    private ParkingEventType operationType;

    @TableField("log_time")
    private LocalDateTime logTime;

    @TableField(value = "detail", typeHandler = ParkingOperationDetailTypeHandler.class)
    private ParkingOperationDetail detail;
}
