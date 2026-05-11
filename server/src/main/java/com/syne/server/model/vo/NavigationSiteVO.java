package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 导航站点VO类
 */
@Data
@Schema(description = "导航站点VO")
public class NavigationSiteVO {

    @Schema(description = "站点ID")
    private Long id;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "站点名称")
    private String name;

    @Schema(description = "站点描述")
    private String description;

    @Schema(description = "站点URL")
    private String url;


    @Schema(description = "排序权重")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}