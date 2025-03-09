package com.smartcommunity.smart_community_platform.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 工单分配结果DTO（内部使用）
 */
@Data
public class TicketAssignDTO {
    @NotNull
    private Long ticketId;

    @NotNull
    private Long assigneeId;
}
