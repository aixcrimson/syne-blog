package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syne.server.model.entity.Article;
import com.syne.server.model.entity.Category;
import com.syne.server.model.entity.Comment;
import com.syne.server.model.entity.User;
import com.syne.server.model.vo.DashboardDataVO;
import com.syne.server.model.vo.DashboardStatsVO;
import com.syne.server.model.vo.DashboardChartsVO;
import com.syne.server.model.vo.MonthlyCountVO;
import com.syne.server.model.vo.MonthlyInteractionVO;
import com.syne.server.model.vo.CategoryDistributionVO;
import com.syne.server.model.vo.TopArticleVO;
import com.syne.server.model.vo.RecentArticleVO;
import com.syne.server.model.vo.RecentCommentVO;
import com.syne.server.mapper.*;
import com.syne.server.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仪表盘服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    @Override
    public DashboardStatsVO getStats() {
        DashboardStatsVO stats = new DashboardStatsVO();

        // 获取文章总数
        LambdaQueryWrapper<Article> articleQuery = new LambdaQueryWrapper<>();
        articleQuery.eq(Article::getStatus, 1); // 只统计已发布的文章
        stats.setArticleCount(articleMapper.selectCount(articleQuery));

        // 获取分类总数
        stats.setCategoryCount(categoryMapper.selectCount(null));

        // 获取标签总数
        stats.setTagCount(tagMapper.selectCount(null));

        // 获取评论总数
        LambdaQueryWrapper<Comment> commentQuery = new LambdaQueryWrapper<>();
        commentQuery.ne(Comment::getStatus, 3); // 排除已删除的文章
        stats.setCommentCount(commentMapper.selectCount(commentQuery));

        // 获取总浏览量
        LambdaQueryWrapper<Article> viewQuery = new LambdaQueryWrapper<>();
        viewQuery.select(Article::getViews);
        List<Article> articles = articleMapper.selectList(viewQuery);
        stats.setTotalViews(articles.stream().mapToLong(a -> a.getViews() != null ? a.getViews() : 0).sum());

        // 获取总点赞数
        LambdaQueryWrapper<Article> likesQuery = new LambdaQueryWrapper<>();
        likesQuery.select(Article::getLikes);
        List<Article> likedArticles = articleMapper.selectList(likesQuery);
        stats.setTotalLikes(likedArticles.stream().mapToLong(a -> a.getLikes() != null ? a.getLikes() : 0).sum());

        return stats;
    };

    @Override
    public List<RecentArticleVO> getRecentArticles(Integer limit) {
        if(limit == null || limit <= 0) {
            limit = 5;
        }

        // 查询最近发布的文章
        LambdaQueryWrapper<Article> query = new LambdaQueryWrapper<>();
        query.eq(Article::getStatus, 1) // 只查询已发布的文章
            .orderByDesc(Article::getPublishedTime)
            .last("LIMIT " + limit);

        List<Article> articles = articleMapper.selectList(query);

        return articles.stream().map(article -> {
            RecentArticleVO vo = new RecentArticleVO();
            vo.setId(article.getId());
            vo.setTitle(article.getTitle());
            vo.setViews(article.getViews());
            vo.setLikes(article.getLikes());
            vo.setPublishedTime(article.getPublishedTime());
            vo.setStatus(article.getStatus());

            // 获取分类名称
            if(article.getCategoryId() != null) {
                Category category = categoryMapper.selectById(article.getCategoryId());
                if(category != null) {
                    vo.setCategoryName(category.getName());
                }
            }

            // 获取评论总数
            LambdaQueryWrapper<Comment> commentQuery = new LambdaQueryWrapper<>();
            commentQuery.ne(Comment::getStatus, 3)
                    .eq(Comment::getArticleId, article.getId()); // 排除已删除的文章
            vo.setCommentCount(commentMapper.selectCount(commentQuery).intValue());

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RecentCommentVO> getRecentComments(Integer limit) {
        if(limit == null || limit <= 0 ){
            limit = 5;
        }

        // 查询最近发布的评论
        LambdaQueryWrapper<Comment> query = new LambdaQueryWrapper<>();
        query.eq(Comment::getStatus, 1) // 状态为发布
        .orderByDesc(Comment::getCreateTime)
        .last("LIMIT " + limit);    
        List<Comment> comments = commentMapper.selectList(query);

        return comments.stream().map(comment -> {
            RecentCommentVO vo = new RecentCommentVO();
            vo.setId(comment.getId());
            vo.setContent(comment.getContent());
            vo.setUserId(comment.getUserId());
            vo.setArticleId(comment.getArticleId());
            vo.setCreateTime(comment.getCreateTime());
            vo.setStatus(comment.getStatus());
            vo.setIpAddress(comment.getIpAddress());

            // 获取用户名
            if(comment.getUserId() != null) {
                User user = userMapper.selectById(comment.getUserId());
                if(user != null) {
                    vo.setUsername(user.getUsername());
                }
            }

            // 获取标题名
            if(comment.getArticleId() != null) {
                Article article = articleMapper.selectById(comment.getArticleId());
                if(article != null) {
                    vo.setArticleTitle(article.getTitle());
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public DashboardDataVO getDashboardData() {
        DashboardDataVO dashboardData = new DashboardDataVO();

        // 获取统计数据
        dashboardData.setStats(getStats());
        dashboardData.setRecentArticles(getRecentArticles(5));
        dashboardData.setRecentComments(getRecentComments(5));

        return dashboardData;
    }

    @Override
    public DashboardChartsVO getChartsData() {
        DashboardChartsVO charts = new DashboardChartsVO();

        // 计算12个月前的起始日期（当月1日零点）
        LocalDateTime startDate = LocalDateTime.now()
                .minusMonths(11)
                .withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        // 1. 文章发布趋势
        List<MonthlyCountVO> articleTrend = articleMapper.selectMonthlyArticleCount(startDate);
        charts.setArticleTrend(fillMissingMonths(articleTrend, startDate));

        // 2. 分类文章分布
        charts.setCategoryDistribution(categoryMapper.selectCategoryArticleDistribution());

        // 3. 热门文章 TOP10
        charts.setTopArticles(articleMapper.selectTopArticlesByViews(10));

        // 4. 互动趋势（浏览 + 点赞 + 评论）
        List<MonthlyInteractionVO> interactions = articleMapper.selectMonthlyInteraction(startDate);
        List<MonthlyCountVO> commentCounts = commentMapper.selectMonthlyCommentCount(startDate);

        // 将评论数合并到互动趋势中
        Map<String, Long> commentMap = commentCounts.stream()
                .collect(Collectors.toMap(MonthlyCountVO::getMonth, MonthlyCountVO::getCount, Long::sum));

        // 补全空月份并合并评论数
        List<MonthlyInteractionVO> filledInteractions = fillMissingInteractionMonths(interactions, startDate);
        for (MonthlyInteractionVO item : filledInteractions) {
            item.setComments(commentMap.getOrDefault(item.getMonth(), 0L));
        }
        charts.setInteractionTrend(filledInteractions);

        return charts;
    }

    /**
     * 补全文章趋势中缺失的月份（用0填充），保证图表连续
     */
    private List<MonthlyCountVO> fillMissingMonths(List<MonthlyCountVO> data, LocalDateTime startDate) {
        Map<String, Long> dataMap = data.stream()
                .collect(Collectors.toMap(MonthlyCountVO::getMonth, MonthlyCountVO::getCount, Long::sum));

        List<MonthlyCountVO> result = new java.util.ArrayList<>();
        LocalDateTime current = startDate;
        LocalDateTime now = LocalDateTime.now();

        while (!current.isAfter(now)) {
            String month = current.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
            MonthlyCountVO vo = new MonthlyCountVO();
            vo.setMonth(month);
            vo.setCount(dataMap.getOrDefault(month, 0L));
            result.add(vo);
            current = current.plusMonths(1);
        }
        return result;
    }

    /**
     * 补全互动趋势中缺失的月份（用0填充），保证图表连续
     */
    private List<MonthlyInteractionVO> fillMissingInteractionMonths(List<MonthlyInteractionVO> data, LocalDateTime startDate) {
        Map<String, MonthlyInteractionVO> dataMap = data.stream()
                .collect(Collectors.toMap(MonthlyInteractionVO::getMonth, v -> v, (a, b) -> a));

        List<MonthlyInteractionVO> result = new java.util.ArrayList<>();
        LocalDateTime current = startDate;
        LocalDateTime now = LocalDateTime.now();

        while (!current.isAfter(now)) {
            String month = current.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
            MonthlyInteractionVO vo = dataMap.get(month);
            if (vo == null) {
                vo = new MonthlyInteractionVO();
                vo.setMonth(month);
                vo.setViews(0L);
                vo.setLikes(0L);
                vo.setComments(0L);
            }
            result.add(vo);
            current = current.plusMonths(1);
        }
        return result;
    }
}
