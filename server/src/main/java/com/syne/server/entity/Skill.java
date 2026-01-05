package com.syne.server.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 技能栈表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("skills")
public class Skill extends BaseEntity {

    /**
     * 技能名称
     */
    private String name;

    /**
     * 技能图标
     */
    private String icon;

    /**
     * 熟练度百分比
     */
    private Integer percentage;

    /**
     * 进度条颜色
     */
    private String color;

    /**
     * 排序权重
     */
    private Integer sortOrder;
}
