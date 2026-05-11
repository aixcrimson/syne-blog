package com.syne.server.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导航分类实体类
 */
@Data
@TableName("navigation_categories")
@Schema(description = "导航分类实体类")
@EqualsAndHashCode(callSuper = true)
public class NavigationCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "分类ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "分类名称")
    @TableField("name")
    private String name;


    @Schema(description = "排序权重")
    @TableField("sort_order")
    private Integer sortOrder;
}