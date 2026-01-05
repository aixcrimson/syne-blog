package com.syne.server.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 精选项目表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("projects")
public class Project extends BaseEntity {

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * GitHub链接
     */
    private String githubUrl;

    /**
     * 预览链接
     */
    private String previewUrl;

    /**
     * 技术栈，逗号分隔
     */
    private String techStack;

    /**
     * 是否推荐/精选: 0-否, 1-是
     */
    private Integer isFeatured;

    /**
     * 排序权重
     */
    private Integer sortOrder;
}
