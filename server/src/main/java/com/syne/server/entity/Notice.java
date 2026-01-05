package com.syne.server.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notices")
public class Notice extends BaseEntity {

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
