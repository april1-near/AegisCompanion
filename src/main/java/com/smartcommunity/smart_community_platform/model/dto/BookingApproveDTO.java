package com.smartcommunity.smart_community_platform.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BookingApproveDTO {
    @NotNull
    private Long bookingId;
    @NotNull
    private Boolean approved;

    @Size(max = 500)
    private String comment;
}
