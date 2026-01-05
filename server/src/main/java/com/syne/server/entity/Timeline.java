package com.syne.server.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 时间线表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("timelines")
public class Timeline extends BaseEntity {

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
