package com.aegis.companion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRecordVO {
    private Long userId;
    private Long id;
    private String purpose;
    private String timeRange;        // 格式化成"2023-12-01 14:00~16:00"
    private String status;
    private String statusStyle;     // 前端显示样式类名
    private String rejectReason;
}
