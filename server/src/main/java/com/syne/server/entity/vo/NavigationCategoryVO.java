package com.syne.server.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 导航分类VO类
 */
@Data
@Schema(description = "导航分类VO")
public class NavigationCategoryVO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "排序权重")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}