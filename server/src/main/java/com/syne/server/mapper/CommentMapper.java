package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.entity.Comment;
import com.syne.server.entity.vo.CommentListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论Mapper接口
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 查询评论列表
     *
     * @param status    评论状态
     * @param articleId 文章ID
     * @param keyword   搜索关键词
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param offset    分页偏移量
     * @param pageSize   分页大小
     * @return 评论列表
     */
    List<CommentListVO> selectCommentList(
        @Param("status") Integer status,
        @Param("articleId") Long articleId,
        @Param("keyword") String keyword,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("offset") Integer offset,
        @Param("pageSize") Integer pageSize
    );

    /**
     * 查询评论数量
     *
     * @param status    评论状态
     * @param articleId 文章ID
     * @param keyword   搜索关键词
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 评论数量
     */
    Long countComments(
        @Param("status") Integer status,
        @Param("articleId") Long articleId,
        @Param("keyword") String keyword,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    /**
     * 查询子评论
     *
     * @param parentId 父评论ID
     * @return 子评论列表
     */
    List<CommentListVO> selectChildComments(@Param("parentId") Long parentId);

    /**
     * 更新文章评论数量
     *
     * @param articleId 文章ID
     * @param increment 增量（正数增加，负数减少）
     */
    void updateArticleCommentCount(@Param("articleId") Long articleId, @Param("increment") Integer increment);
}