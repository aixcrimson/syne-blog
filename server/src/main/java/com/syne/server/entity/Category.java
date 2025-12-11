package com.syne.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分类实体类
 */
@Data
@TableName("categories")
@Schema(description = "分类实体类")
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "分类ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "分类名称")
    @TableField("name")
    private String name;

    @Schema(description = "URL友好的分类标识")
    @TableField("slug")
    private String slug;

    @Schema(description = "分类描述")
    @TableField("description")
    private String description;

    @Schema(description = "分类图标")
    @TableField("icon")
    private String icon;

    @Schema(description = "排序权重，数字越大越靠前")
    @TableField("sort_order")
    private Integer sortOrder;
}