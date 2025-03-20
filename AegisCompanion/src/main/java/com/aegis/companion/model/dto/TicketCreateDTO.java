package com.aegis.companion.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 工单创建请求DTO
 */
@Data
public class TicketCreateDTO {
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题最长100字符")
    private String title;

    @NotBlank(message = "问题描述不能为空")
    private String description;

    @NotBlank(message = "工单类型不能为空")
    private String type;
}
