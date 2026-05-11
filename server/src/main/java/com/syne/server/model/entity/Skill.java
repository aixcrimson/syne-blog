package com.syne.server.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 技能栈表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "技能栈表")
@TableName("skills")
public class Skill extends BaseEntity {

    private final static long serialVersionUID = 1L;

    @Schema(description = "技能ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 技能名称
     */
    private String name;


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
