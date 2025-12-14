package com.syne.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签实体类
 */
@Data
@TableName("tags")
@Schema(description = "标签实体类")
@EqualsAndHashCode(callSuper = true)
public class Tags extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "标签ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "标签名称")
    @TableField("name")
    private String name;

    @Schema(description = "URL友好的标签标识")
    @TableField("slug")
    private String slug;

    @Schema(description = "标签颜色，用于前端显示")
    @TableField("color")
    private String color;

    @Schema(description = "标签使用次数统计")
    @TableField("usage_count")
    private Integer usageCount;
}