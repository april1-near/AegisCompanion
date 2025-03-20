package com.smartcommunity.smart_community_platform.model.vo;

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
public class BookingRecordAdminVO {

    private Long id;
    private String userName;
    private String roomName;
    private String purpose;
    private Integer participantCount;
    private String timeRange;        // 已格式化成"2023-12-01 14:00~16:00"
    private String bookingStatus;
}
