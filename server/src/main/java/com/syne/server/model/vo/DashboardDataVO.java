package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 仪表盘完整数据VO
 */
@Data
@Schema(description = "仪表盘完整数据")
public class DashboardDataVO {

    @Schema(description = "统计数据")
    private DashboardStatsVO stats;

    @Schema(description = "最近文章列表")
    private List<RecentArticleVO> recentArticles;

    @Schema(description = "最近评论列表")
    private List<RecentCommentVO> recentComments;
}