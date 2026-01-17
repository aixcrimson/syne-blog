package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 最近文章VO（仪表盘用）
 */
@Data
@Schema(description = "最近文章")
public class RecentArticleVO {

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "浏览量")
    private Integer views;

    @Schema(description = "点赞数")
    private Integer likes;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "发布时间")
    private LocalDateTime publishedTime;

    @Schema(description = "文章状态")
    private Integer status;

    @Schema(description = "分类名称")
    private String categoryName;
}