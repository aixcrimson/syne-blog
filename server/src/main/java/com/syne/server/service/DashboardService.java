package com.syne.server.service;

import com.syne.server.entity.vo.DashboardDataVO;
import com.syne.server.entity.vo.DashboardStatsVO;
import com.syne.server.entity.vo.RecentArticleVO;
import com.syne.server.entity.vo.RecentCommentVO;

import java.util.List;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {

    /**
     * 获取仪表盘统计数据
     *
     * @return 统计数据
     */
    DashboardStatsVO getStats();

    /**
     * 获取最近发布的文章列表
     *
     * @param limit 返回数量限制
     * @return 最近文章列表
     */
    List<RecentArticleVO> getRecentArticles(Integer limit);

    /**
     * 获取最近的评论列表
     *
     * @param limit 返回数量限制
     * @return 最近评论列表
     */
    List<RecentCommentVO> getRecentComments(Integer limit);

    /**
     * 获取仪表盘数据
     * 
     * @return 仪表盘数据
     */
    DashboardDataVO getDashboardData();
}