package com.syne.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章实体类
 */
@Data
@TableName("articles")
@Schema(description = "文章实体")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "文章ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

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
    @TableField("published_at")
    private LocalDateTime publishedAt;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除: 0-未删除, 1-已删除")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}
