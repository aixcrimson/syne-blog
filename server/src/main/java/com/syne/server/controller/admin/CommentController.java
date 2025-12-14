package com.syne.server.controller.admin;

import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.entity.Comment;
import com.syne.server.entity.vo.CommentListVO;
import com.syne.server.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 管理员评论控制器
 */
@Slf4j
@Tag(name = "管理员评论管理", description = "管理员评论相关接口")
@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;

    /**
     * 分页查询评论列表
     *
     * @param page      页码
     * @param pageSize  每页大小
     * @param status    评论状态
     * @param articleId 文章ID
     * @param keyword   搜索关键词
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 评论分页列表
     */
    @Operation(summary = "查询评论列表", description = "管理员分页查询评论列表，支持按状态、文章、关键词筛选")
    @GetMapping
    public Result<PageResult<CommentListVO>> getCommentList(
        @Parameter(description = "页码", example = "1")
        @RequestParam(defaultValue = "1") Integer page,

        @Parameter(description = "每页大小", example = "10")
        @RequestParam(defaultValue = "10") Integer pageSize,

        @Parameter(description = "评论状态（1-正常，2-待审核，3-已删除）", example = "1")
        @RequestParam(required = false) Integer status,

        @Parameter(description = "文章ID", example = "1")
        @RequestParam(required = false) Long articleId,

        @Parameter(description = "搜索关键词", example = "Vue")
        @RequestParam(required = false) String keyword,

        @Parameter(description = "开始时间", example = "2025-12-01 00:00:00")
        @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,

        @Parameter(description = "结束时间", example = "2025-12-31 23:59:59")
        @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime
    ) {
        log.info("分页查询评论列表：page={}, pageSize={}, status={}, articleId={}, keyword={}, startTime={}, endTime={}",
                page, pageSize, status, articleId, keyword, startTime, endTime);

        PageQuery pageQuery = new PageQuery();
        pageQuery.setPage(page);
        pageQuery.setPageSize(pageSize);

        PageResult<CommentListVO> result = commentService.getCommentList(
            pageQuery, status, articleId, keyword, startTime, endTime);

        return Result.success(result);
    }

    /**
     * 根据ID获取评论详情
     *
     * @param id 评论ID
     * @return 评论详情
     */
    @Operation(summary = "获取评论详情", description = "管理员根据ID获取评论详情，包含完整回复链")
    @GetMapping("/{id}")
    public Result<Comment> getCommentById(
        @Parameter(description = "评论ID", required = true)
        @PathVariable Long id
    ) {
        log.info("获取评论详情：id={}", id);
        Comment comment = commentService.getCommentById(id);
        return Result.success(comment);
    }

    /**
     * 删除评论
     *
     * @param ids 评论ID字符串
     * @return 删除结果
     */
    @Operation(summary = "删除评论", description = "删除单个或多个评论（逻辑删除），支持批量操作")
    @DeleteMapping
    public Result<String> deleteComments(
        @Parameter(description = "评论ID，单个或多个用英文逗号分隔", required = true, example = "1,2,3")
        @RequestParam String ids
    ) {
        log.info("删除评论：ids={}", ids);
        Result<String> result = commentService.deleteComments(ids);
        return result;
    }
}