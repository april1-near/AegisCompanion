// RoomUpdateDTO.java
package com.aegis.companion.model.dto;

import com.aegis.companion.model.enums.RoomTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Data
@Schema(description = "更新活动室DTO")
public class RoomUpdateDTO {
    @NotNull(message = "活动室ID不能为空")
    @Schema(description = "活动室ID", example = "1")
    private Long id;

    @NotBlank(message = "活动室名称不能为空")
    @Schema(description = "活动室名称", example = "多功能厅A")
    private String roomName;

    @NotNull(message = "活动室类型不能为空")
    @Schema(description = "活动室类型")
    private RoomTypeEnum roomType;

    @NotNull(message = "最大容量不能为空")
    @Schema(description = "最大容量", example = "50")
    private Integer maxCapacity;

    @NotNull(message = "开放时间不能为空")
    @Schema(description = "开放时间", example = "08:00")
    private LocalTime openHour;

    @NotNull(message = "关闭时间不能为空")
    @Schema(description = "关闭时间", example = "22:00")
    private LocalTime closeHour;

    @NotNull(message = "启用状态不能为空")
    @Schema(description = "是否启用")
    private Boolean isActive;
}
