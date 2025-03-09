package com.smartcommunity.smart_community_platform.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DoctorVO {
    private Long id;
    private String name;
    private String title;
    private String introduction;
}