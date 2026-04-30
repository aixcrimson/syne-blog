package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.syne.server.model.entity.Tags;
import java.util.List;

/**
 * 文章列表视图对象
 */
@Data
@Schema(description = "文章列表视图对象")
public class ArticleListVO implements Serializable {

    public static final long serialVersionUID = 1L;

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "文章图片")
    private String coverImage;

    @Schema(description = "浏览量")
    private Integer views;

    @Schema(description = "点赞数")
    private Integer likes;

    @Schema(description = "收藏数")
    private Integer favorites;

    @Schema(description = "评论数")
    private Integer commentsCount;

    @Schema(description = "是否置顶")
    private Integer isTop;



    @Schema(description = "文章状态")
    private Integer status;

    @Schema(description = "发布时间")
    private LocalDateTime publishedTime;

    @Schema(description = "作者名称")
    private String authorName;

    @Schema(description = "作者头像")
    private String authorAvatar;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "标签列表")
    private List<Tags> tags;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}