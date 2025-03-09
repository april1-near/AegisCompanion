package com.smartcommunity.smart_community_platform.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ParkingOperationDetail {
    private String oldStatus;
    private String newStatus;
    private String remark;
}