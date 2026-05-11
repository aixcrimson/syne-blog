package com.syne.server.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导航站点实体类
 */
@Data
@TableName("navigation_sites")
@Schema(description = "导航站点实体类")
@EqualsAndHashCode(callSuper = true)
public class NavigationSite extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "站点ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "分类ID")
    @TableField("category_id")
    private Long categoryId;

    @Schema(description = "站点名称")
    @TableField("name")
    private String name;

    @Schema(description = "站点描述")
    @TableField("description")
    private String description;

    @Schema(description = "站点URL")
    @TableField("url")
    private String url;


    @Schema(description = "排序权重")
    @TableField("sort_order")
    private Integer sortOrder;
}