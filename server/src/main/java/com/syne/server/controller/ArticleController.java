package com.syne.server.controller;

import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.entity.vo.ArticleListVO;
import com.syne.server.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文章控制器
 */
@Tag(name = "文章管理", description = "文章相关接口")
@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 分页查询文章列表
     */
    @Operation(summary = "查询文章列表", description = "分页查询文章列表，支持按状态筛选")
    @GetMapping
    public Result<PageResult<ArticleListVO>> getArticleList(
        @Parameter(description = "页码", example = "1")
        @RequestParam(defaultValue = "1") Integer page,

        @Parameter(description = "每页大小", example = "10")
        @RequestParam(defaultValue = "10") Integer pageSize,

        @Parameter(description = "文章状态（1-已发布，2-草稿，3-已下架", example = "1")
        @RequestParam(required = false) Integer status
    ){
        return null;
    }
}