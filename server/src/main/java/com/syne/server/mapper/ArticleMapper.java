package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.model.entity.Article;
import com.syne.server.model.vo.ArticleListVO;
import com.syne.server.model.vo.MonthlyCountVO;
import com.syne.server.model.vo.MonthlyInteractionVO;
import com.syne.server.model.vo.TopArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章 Mapper 接口
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 管理员查询文章列表
     * @param offset   偏移量
     * @param pageSize 每页大小
     * @param status   文章状态（可选）
     * @param categoryId 文章分类ID（可选）
     * @param keyword  搜索关键词（可选）
     * @return 文章列表
     */
    List<ArticleListVO> selectAdminArticleList(
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize,
            @Param("status") Integer status,
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword
    );

    /**
     * 查询文章总数
     * @param status 文章状态（可选）
     * @param categoryId 文章分类ID（可选）
     * @param keyword  搜索关键词（可选）
     * @return 总数
     */
    Long countAdminArticles(@Param("status") Integer status, @Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    /**
     * 用户端查询文章列表（包含作者、分类、标签信息）
     * @param offset   偏移量
     * @param pageSize 每页大小
     * @param keyword  搜索关键词
     * @param categoryId  分类ID
     * @param tagIds  标签ID列表
     * @return 文章列表
     */
    List<ArticleListVO> selectUserArticleList(
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize,
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("tagIds") List<Long> tagIds
    );

    /**
     * 查询文章总数
     * @param keyword  搜索关键词
     * @param categoryId  分类ID
     * @param tagIds  标签ID列表
     * @return 总数
     */
    Long countUserArticles(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("tagIds") List<Long> tagIds
    );

    /**
     * 查询用户点赞的文章列表
     */
    List<ArticleListVO> selectLikedArticles(
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize,
            @Param("userId") Long userId
    );

    Long countLikedArticles(@Param("userId") Long userId);

    /**
     * 查询用户收藏的文章列表
     */
    List<ArticleListVO> selectFavoriteArticles(
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize,
            @Param("userId") Long userId
    );

    Long countFavoriteArticles(@Param("userId") Long userId);

    // ==================== 仪表盘图表查询 ====================

    /**
     * 按月统计已发布文章数量（从 startDate 起）
     *
     * @param startDate 起始日期
     * @return 月度文章发布数列表
     */
    List<MonthlyCountVO> selectMonthlyArticleCount(@Param("startDate") LocalDateTime startDate);

    /**
     * 按浏览量降序获取热门文章 TOP N
     *
     * @param limit 数量限制
     * @return 热门文章列表
     */
    List<TopArticleVO> selectTopArticlesByViews(@Param("limit") Integer limit);

    /**
     * 按月汇总已发布文章的浏览量和点赞量（从 startDate 起）
     *
     * @param startDate 起始日期
     * @return 月度互动数据列表（不含评论，评论需从 CommentMapper 获取）
     */
    List<MonthlyInteractionVO> selectMonthlyInteraction(@Param("startDate") LocalDateTime startDate);
}
