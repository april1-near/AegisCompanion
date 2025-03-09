package com.smartcommunity.smart_community_platform.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

// PageResult.java
@Data
@Accessors(chain = true)
@Schema(description = "分页查询通用响应结构")
public class PageResult<T> {
    private Long total;
    private List<T> records;
}