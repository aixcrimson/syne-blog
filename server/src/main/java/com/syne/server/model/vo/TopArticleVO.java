package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 热门文章VO
 * 用于仪表盘柱状图展示浏览量TOP文章
 */
@Data
@Schema(description = "热门文章")
public class TopArticleVO {

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "浏览量")
    private Integer views;
}
