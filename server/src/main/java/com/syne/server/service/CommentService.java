package com.syne.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.entity.Comment;
import com.syne.server.entity.dto.CommentCreateDTO;
import com.syne.server.entity.vo.CommentListVO;
import com.syne.server.entity.vo.CommentShowVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<Comment> {

    /**
     * 分页查询评论列表
     *
     * @param pageQuery  分页查询参数
     * @param status     评论状态
     * @param articleId  文章ID
     * @param keyword    搜索关键词
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 评论分页列表
     */
    PageResult<CommentListVO> getCommentList(PageQuery pageQuery, Integer status, Long articleId,
                                             String keyword, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据ID获取评论详情
     *
     * @param id 评论ID
     * @return 评论详情
     */
    Comment getCommentById(Long id);

    /**
     * 删除评论
     *
     * @param ids 评论ID字符串
     * @return 删除结果
     */
    Result<String> deleteComments(String ids);

    // ==================== 用户端接口 ====================

    /**
     * 获取文章评论列表（用户端，树形结构）
     *
     * @param articleId 文章ID
     * @param pageQuery 分页参数
     * @return 评论列表（树形结构）
     */
    PageResult<CommentShowVO> getArticleComments(Long articleId, PageQuery pageQuery);

    /**
     * 创建评论
     *
     * @param commentCreateDTO 评论创建参数
     * @param ipAddress 用户IP地址
     * @return 创建的评论
     */
    CommentShowVO createComment(CommentCreateDTO commentCreateDTO, String ipAddress);
}