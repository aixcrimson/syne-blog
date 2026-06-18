package com.syne.server.controller.admin;

import com.syne.server.common.Result;
import com.syne.server.model.vo.DashboardDataVO;
import com.syne.server.model.vo.DashboardStatsVO;
import com.syne.server.model.vo.DashboardChartsVO;
import com.syne.server.model.vo.RecentArticleVO;
import com.syne.server.model.vo.RecentCommentVO;
import com.syne.server.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理员仪表盘控制器
 */
@Slf4j
@Tag(name = "仪表盘", description = "仪表盘接口")
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@Validated
public class DashboardController {
    
    private final DashboardService dashboardService;

    /**
     * 获取仪表盘统计数据
     *
     * @return 统计数据
     */
    @Operation(summary = "获取统计数据", description = "获取文章、分类、标签、评论、浏览量、点赞数等统计数据")
    @GetMapping("/stats")
    public Result<DashboardStatsVO> getStats() {
        log.info("获取仪表盘统计数据");
        DashboardStatsVO stats = dashboardService.getStats();
        return Result.success(stats);
    }

    /**
     * 获取最近发布的文章列表
     *
     * @param limit 返回数量限制，默认5
     * @return 最近文章列表
     */
    @Operation(summary = "获取最近文章", description = "获取最近发布的文章列表，默认返回5条")
    @GetMapping("/recent-articles")
    public Result<List<RecentArticleVO>> getRecentArticles(
            @Parameter(description = "返回数量限制", example = "5")
            @RequestParam(defaultValue = "5") Integer limit) {
        log.info("获取最近文章列表，数量限制: {}", limit);
        List<RecentArticleVO> articles = dashboardService.getRecentArticles(limit);
        return Result.success(articles);
    }

    /**
     * 获取最近的评论列表
     *
     * @param limit 返回数量限制，默认5
     * @return 最近评论列表
     */
    @Operation(summary = "获取最近评论", description = "获取最近的评论列表，默认返回5条")
    @GetMapping("/recent-comments")
    public Result<List<RecentCommentVO>> getRecentComments(
            @Parameter(description = "返回数量限制", example = "5")
            @RequestParam(defaultValue = "5") Integer limit) {
        log.info("获取最近评论列表，数量限制: {}", limit);
        List<RecentCommentVO> comments = dashboardService.getRecentComments(limit);
        return Result.success(comments);
    }

    /**
     * 获取仪表盘数据
     * 
     * @return 仪表盘数据
     */
    @Operation(summary = "获取仪表盘数据", description = "获取仪表盘数据，包括统计数据、最近文章列表、最近评论列表")
    @GetMapping
    public Result<DashboardDataVO> getDashboardData() {
        log.info("获取仪表盘数据");
        DashboardDataVO dashboardData = dashboardService.getDashboardData();
        return Result.success(dashboardData);
    }

    /**
     * 获取仪表盘图表数据
     *
     * @return 图表数据（文章趋势、分类分布、热门文章、互动趋势）
     */
    @Operation(summary = "获取图表数据", description = "获取仪表盘图表数据，包括文章发布趋势、分类分布、热门文章TOP10、互动趋势")
    @GetMapping("/charts")
    public Result<DashboardChartsVO> getChartsData() {
        log.info("获取仪表盘图表数据");
        DashboardChartsVO chartsData = dashboardService.getChartsData();
        return Result.success(chartsData);
    }
}