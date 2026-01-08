package com.syne.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 时间线实体类
 */
@Data
@Schema(description = "时间线实体类")
@EqualsAndHashCode(callSuper = true)
@TableName("timelines")
public class Timeline extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "时间线ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 年份或时间点
     */
    private String year;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 图标名称
     */
    private String icon;

    /**
     * 节点颜色类型
     */
    private String color;

    /**
     * 排序权重
     */
    private Integer sortOrder;
}
