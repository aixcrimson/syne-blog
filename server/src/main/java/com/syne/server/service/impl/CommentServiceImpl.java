package com.syne.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.entity.Article;
import com.syne.server.entity.Comment;
import com.syne.server.entity.dto.CommentCreateDTO;
import com.syne.server.entity.vo.CommentListVO;
import com.syne.server.entity.vo.CommentShowVO;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.ArticleMapper;
import com.syne.server.mapper.CommentMapper;
import com.syne.server.service.CommentService;
import com.syne.server.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;

    @Override
    public PageResult<CommentListVO> getCommentList(PageQuery pageQuery, Integer status, Long articleId,
                                                    String keyword, LocalDateTime startTime, LocalDateTime endTime) {
        // 查询评论列表
        List<CommentListVO> list = commentMapper.selectCommentList(
                status,
                articleId,
                keyword,
                startTime,
                endTime,
                pageQuery.getOffset(),
                pageQuery.getPageSize()
        );

        // 查询总数
        Long total = commentMapper.countComments(status, articleId, keyword, startTime, endTime);

        // 构建分页结果返回
        return PageResult.build(
                pageQuery.getPage(),
                pageQuery.getPageSize(),
                total,
                list
        );
    }

    @Override
    public Comment getCommentById(Long id) {
        Comment comment = this.getById(id);
        if (comment == null || comment.getDeleted() == 1) {
            throw new BusinessException("评论不存在");
        }

        return comment;
    }

    @Override
    @Transactional
    public Result<String> deleteComments(String ids) {
        log.info("删除评论：ids={}", ids);
        if (!StringUtils.hasText(ids)) {
            throw new BusinessException("评论ID不能为空");
        }

        String[] idArray = ids.split(",");
        if (idArray.length > 100) {
            throw new BusinessException("单次删除数量不能超过100个");
        }

        List<String> failedIds = new ArrayList<>();
        int successCount = 0;
        int totalDeletedCount = 0;

        for (String idStr : idArray) {
            try {
                Long id = Long.parseLong(idStr.trim());
                int deletedCount = deleteComment(id);
                successCount++;
                totalDeletedCount += deletedCount;
            } catch (NumberFormatException e) {
                failedIds.add(idStr + "(格式错误)");
                log.warn("无效的评论ID格式：{}", idStr);
            } catch (BusinessException e) {
                failedIds.add(idStr + "(" + e.getMessage() + ")");
                log.warn("删除评论失败：id={}, 原因={}", idStr, e.getMessage());
            }
        }

        // 构建返回消息
        String message;
        if (failedIds.isEmpty()) {
            message = successCount == 1 ? "评论删除成功" : "成功删除 " + totalDeletedCount + " 条评论";
            return Result.success(message);
        } else {
            message = "成功删除 " + totalDeletedCount + " 条评论，失败：" + String.join(", ", failedIds);
            return Result.success(message);
        }
    }

    /**
     * 删除单个评论及其子评论
     *
     * @param id 评论ID
     * @return 删除的评论数量
     */
    private int deleteComment(Long id) {
        Comment comment = this.getById(id);
        if (comment == null || comment.getDeleted() == 1) {
            throw new BusinessException("评论不存在");
        }

        int deletedCount = 0;

        // 删除该评论
        comment.setDeleted(1);
        this.updateById(comment);
        deletedCount++;

        // 递归删除所有子评论
        List<CommentListVO> children = commentMapper.selectChildComments(id);
        for (CommentListVO child : children) {
            deletedCount += deleteComment(child.getId());
        }

        // 更新文章评论计数
        commentMapper.updateArticleCommentCount(comment.getArticleId(), -deletedCount);

        return deletedCount;
    }

    // ==================== 用户端接口实现 ====================

    @Override
    public PageResult<CommentShowVO> getArticleComments(Long articleId, PageQuery pageQuery) {
        // 验证文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null || article.getDeleted() == 1) {
            throw new BusinessException("文章不存在");
        }

        // 查询顶级评论
        List<CommentShowVO> topComments = commentMapper.selectArticleTopComments(
                articleId,
                pageQuery.getOffset(),
                pageQuery.getPageSize()
        );

        // 为每个顶级评论加载子评论（递归）
        for (CommentShowVO topComment : topComments) {
            loadReplies(topComment);
        }

        // 查询顶级评论总数
        Long total = commentMapper.countArticleTopComments(articleId);

        return PageResult.build(
                pageQuery.getPage(),
                pageQuery.getPageSize(),
                total,
                topComments
        );
    }

    /**
     * 递归加载评论的回复
     *
     * @param comment 评论
     */
    private void loadReplies(CommentShowVO comment) {
        List<CommentShowVO> replies = commentMapper.selectCommentReplies(comment.getId());
        if (replies != null && !replies.isEmpty()) {
            for (CommentShowVO reply : replies) {
                loadReplies(reply);  // 递归加载子回复
            }
            comment.setChildren(replies);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentShowVO createComment(CommentCreateDTO commentCreateDTO, String ipAddress) {
        // 验证文章是否存在
        Article article = articleMapper.selectById(commentCreateDTO.getArticleId());
        if (article == null || article.getDeleted() == 1) {
            throw new BusinessException("文章不存在");
        }

        // 如果是回复，验证父评论是否存在
        Comment parentComment = null;
        if (commentCreateDTO.getParentId() != null) {
            parentComment = this.getById(commentCreateDTO.getParentId());
            if (parentComment == null || parentComment.getDeleted() == 1) {
                throw new BusinessException("父评论不存在");
            }
            // 验证父评论是否属于同一文章
            if (!parentComment.getArticleId().equals(commentCreateDTO.getArticleId())) {
                throw new BusinessException("父评论不属于该文章");
            }
        }

        // 获取当前用户ID（如果已登录）
        Long userId = SecurityUtils.getCurrentUserId();

        // 创建评论
        Comment comment = new Comment();
        comment.setArticleId(commentCreateDTO.getArticleId());
        comment.setUserId(userId);
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setIpAddress(ipAddress);
        comment.setStatus(1);  // 默认状态：1-正常（可以改为待审核）
        comment.setDeleted(0);

        this.save(comment);

        // 更新文章评论数量
        commentMapper.updateArticleCommentCount(commentCreateDTO.getArticleId(), 1);

        log.info("创建评论成功：id={}, articleId={}, userId={}", comment.getId(), comment.getArticleId(), userId);

        // 构建返回VO
        CommentShowVO commentShowVO = new CommentShowVO();
        commentShowVO.setId(comment.getId());
        commentShowVO.setArticleId(comment.getArticleId());
        commentShowVO.setUserId(userId);
        commentShowVO.setUsername(userId != null ? SecurityUtils.getCurrentUsername() : "访客");
        commentShowVO.setUserAvatar(null); // 如需要可查询用户头像
        commentShowVO.setParentId(comment.getParentId());
        commentShowVO.setContent(comment.getContent());
        commentShowVO.setCreateTime(comment.getCreateTime());

        // 设置被回复用户的用户名
        if (parentComment != null && parentComment.getUserId() != null) {
            // 需要查询被回复用户的用户名，这里简化处理
            commentShowVO.setReplyToUsername(null);
        }

        return commentShowVO;
    }
}