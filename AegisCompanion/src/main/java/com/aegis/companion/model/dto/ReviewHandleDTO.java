package com.aegis.companion.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class ReviewHandleDTO {
    @NotNull(message = "审核结果不能为空")
    private Boolean isApproved;

    @NotBlank(message = "审核备注在驳回时必填")
    private String remark;
}
