package com.syne.server.entity.vo;

import com.syne.server.entity.Tags;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理端文章详情视图对象
 */
@Data
@Schema(description = "管理端文章详情视图对象")
public class ArticleDetailVO implements Serializable {

    public static final long serialVersionUID = 1L;

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "文章内容")
    private String content;

    @Schema(description = "封面图片")
    private String coverImage;

    @Schema(description = "浏览量")
    private Integer views;

    @Schema(description = "点赞数")
    private Integer likes;

    @Schema(description = "收藏数")
    private Integer favorites;

    @Schema(description = "评论数")
    private Integer commentsCount;

    @Schema(description = "文章状态: 1-已发布, 2-草稿, 3-已下架")
    private Integer status;

    @Schema(description = "是否置顶: 0-否, 1-是")
    private Integer isTop;

    @Schema(description = "是否推荐: 0-否, 1-是")
    private Integer isRecommend;

    @Schema(description = "发布时间")
    private LocalDateTime publishedTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "标签列表")
    private List<Tags> tags;

    @Schema(description = "是否已点赞")
    private Boolean isLiked;

    @Schema(description = "是否已收藏")
    private Boolean isFavorited;
}