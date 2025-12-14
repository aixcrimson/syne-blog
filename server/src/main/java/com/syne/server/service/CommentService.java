package com.syne.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.entity.Comment;
import com.syne.server.entity.vo.CommentListVO;

import java.time.LocalDateTime;

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
}