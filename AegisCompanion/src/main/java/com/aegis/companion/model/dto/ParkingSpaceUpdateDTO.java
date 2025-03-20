package com.aegis.companion.model.dto;


import com.aegis.companion.model.enums.ParkingSpaceStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingSpaceUpdateDTO {
    @NotNull(message = "车位ID不能为空")
    private Long id;

    @NotBlank(message = "区域编码不能为空")
    @Pattern(regexp = "^[A-D]$", message = "区域编码必须为A-D的1位大写字母")
    private String zoneCode;

    @NotBlank(message = "车位编号不能为空")
    private String number;

    @NotNull(message = "版本号不能为空")
    private Integer version;

    private ParkingSpaceStatus status;
    private String qrCode;
}


