package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 仪表盘图表数据聚合VO
 * 一次性返回所有图表所需数据，减少网络请求
 */
@Data
@Schema(description = "仪表盘图表数据")
public class DashboardChartsVO {

    @Schema(description = "文章发布趋势：最近12个月每月发布数")
    private List<MonthlyCountVO> articleTrend;

    @Schema(description = "分类文章分布")
    private List<CategoryDistributionVO> categoryDistribution;

    @Schema(description = "热门文章TOP10")
    private List<TopArticleVO> topArticles;

    @Schema(description = "互动趋势：最近12个月浏览/点赞/评论数")
    private List<MonthlyInteractionVO> interactionTrend;
}
