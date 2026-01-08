package com.syne.server.controller.web;

import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.entity.dto.CommentCreateDTO;
import com.syne.server.entity.vo.CommentShowVO;
import com.syne.server.service.CommentService;
import com.syne.server.utils.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端评论控制器
 */
@Slf4j
@Tag(name = "用户端评论管理", description = "用户端评论相关接口")
@RestController("webCommentController")
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 获取文章评论列表
     *
     * @param articleId 文章ID
     * @param page      页码
     * @param pageSize  每页大小
     * @return 评论列表（树形结构）
     */
    @Operation(summary = "获取文章评论列表", description = "获取文章的评论列表，支持分页，返回树形结构")
    @GetMapping("/article/{articleId}")
    public Result<PageResult<CommentShowVO>> getArticleComments(
            @Parameter(description = "文章ID", required = true)
            @PathVariable Long articleId,

            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        log.info("获取文章评论列表：articleId={}, page={}, pageSize={}", articleId, page, pageSize);

        PageQuery pageQuery = new PageQuery();
        pageQuery.setPage(page);
        pageQuery.setPageSize(pageSize);

        PageResult<CommentShowVO> result = commentService.getArticleComments(articleId, pageQuery);
        return Result.success(result);
    }

    /**
     * 创建评论
     *
     * @param commentCreateDTO 评论创建参数
     * @param request          HTTP请求
     * @return 创建的评论
     */
    @Operation(summary = "创建评论", description = "发表评论或回复评论")
    @PostMapping
    public Result<CommentShowVO> createComment(
            @Valid @RequestBody CommentCreateDTO commentCreateDTO,
            HttpServletRequest request
    ) {
        String ipAddress = IpUtils.getClientIp(request);
        log.info("创建评论：articleId={}, parentId={}, ip={}",
                commentCreateDTO.getArticleId(),
                commentCreateDTO.getParentId(),
                ipAddress);

        CommentShowVO comment = commentService.createComment(commentCreateDTO, ipAddress);
        return Result.success(comment);
    }
}
