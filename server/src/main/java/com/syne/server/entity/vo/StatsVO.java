package com.syne.server.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计数据VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统计数据响应")
public class StatsVO {

    @Schema(description = "文章总数")
    private Long totalArticles;

    @Schema(description = "分类总数")
    private Long totalCategories;

    @Schema(description = "文章浏览总数")
    private Long totalViews;
}