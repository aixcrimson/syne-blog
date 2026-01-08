package com.syne.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "公告表")
@TableName("notices")
public class Notice extends BaseEntity {

    private final static long serialVersionUID = 1L;

    @Schema(description = "公告ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 是否显示: 0-否, 1-是
     */
    private Integer isShow;

    /**
     * 排序权重
     */
    private Integer sortOrder;
}
