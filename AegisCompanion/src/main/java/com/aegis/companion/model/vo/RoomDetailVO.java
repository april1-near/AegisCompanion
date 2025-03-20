package com.aegis.companion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RoomDetailVO {
    private Long id;
    private String roomName;
    private String roomType;
    private String typeDescription;  // 枚举描述
    private Integer maxCapacity;
    private String timeRange;        // 格式化成"08:00-22:00"
    private Boolean isAvailable;
}
