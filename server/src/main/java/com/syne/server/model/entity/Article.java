package com.syne.server.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文章实体类
 */
@Data
@TableName("articles")
@Schema(description = "文章实体")
@EqualsAndHashCode(callSuper = true)
public class Article extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "文章ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "分类ID")
    @TableField("category_id")
    private Long categoryId;

    @Schema(description = "文章标题")
    @TableField("title")
    private String title;

    @Schema(description = "文章摘要")
    @TableField("summary")
    private String summary;

    @Schema(description = "文章内容")
    @TableField("content")
    private String content;

    @Schema(description = "封面图片")
    @TableField("cover_image")
    private String coverImage;

    @Schema(description = "浏览量")
    @TableField("views")
    private Integer views;

    @Schema(description = "点赞数")
    @TableField("likes")
    private Integer likes;

    @Schema(description = "收藏数")
    @TableField("favorites")
    private Integer favorites;

    @Schema(description = "评论数")
    @TableField("comments_count")
    private Integer commentsCount;

    @Schema(description = "文章状态: 1-已发布, 2-草稿, 3-已下架")
    @TableField("status")
    private Integer status;

    @Schema(description = "是否置顶: 0-否, 1-是")
    @TableField("is_top")
    private Integer isTop;

    @Schema(description = "是否推荐: 0-否, 1-是")
    @TableField("is_recommend")
    private Integer isRecommend;

    @Schema(description = "发布时间")
    @TableField("published_time")
    private LocalDateTime publishedTime;

}
