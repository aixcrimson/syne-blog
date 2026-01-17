package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 仪表盘统计数据VO
 * 用于展示仪表盘页面的各项统计信息
 */
@Data
@Schema(description = "仪表盘统计数据")
public class DashboardStatsVO {

    @Schema(description = "文章总数")
    private Long articleCount;

    @Schema(description = "分类总数")
    private Long categoryCount;

    @Schema(description = "标签总数")
    private Long tagCount;

    @Schema(description = "评论总数")
    private Long commentCount;

    @Schema(description = "总浏览量")
    private Long totalViews;

    @Schema(description = "总点赞数")
    private Long totalLikes;

    @Schema(description = "用户总数")
    private Long userCount;
}