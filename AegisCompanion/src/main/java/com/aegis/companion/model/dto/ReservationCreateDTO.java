// 文件：ReservationCreateDTO.java
package com.aegis.companion.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 预约创建请求参数
 */
@Data
public class ReservationCreateDTO {
    @NotNull(message = "车位ID不能为空")
    private Long spaceId;
}
