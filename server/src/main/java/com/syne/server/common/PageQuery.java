package com.syne.server.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;

/**
 * 分页查询参数
 */
@Data
@Schema(description = "分页查询参数")
public class PageQuery {

    @Schema(description = "页码（从1开始）", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize = 10;

    @Schema(description = "排序字段", example = "create_time")
    private String sortField;

    @Schema(description = "排序方式：asc-升序, desc-降序", example = "desc")
    private String sortOrder;

    /**
     * 获取偏移量（用于 SQL OFFSET）
     */
    public Integer getOffset() {
        return (page - 1) * pageSize;
    }

}