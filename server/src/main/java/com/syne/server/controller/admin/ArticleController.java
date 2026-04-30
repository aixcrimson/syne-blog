package com.syne.server.controller.admin;

import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.model.entity.Article;
import com.syne.server.model.dto.ArticleDTO;
import com.syne.server.model.vo.ArticleDetailVO;
import com.syne.server.model.vo.ArticleListVO;
import com.syne.server.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员文章控制器
 */
@Slf4j
@Tag(name = "管理员文章管理", description = "管理员文章相关接口")
@RestController
@RequestMapping("/admin/articles")
@RequiredArgsConstructor
@Validated
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 分页查询文章列表
     *
     * @param page 页码
     * @param pageSize 每页大小
     * @param status 文章状态（可选）
     * @param categoryId 文章分类ID（可选）
     * @param keyword 搜索关键词（可选）
     * @return 文章分页列表
     */
    @Operation(summary = "查询文章列表", description = "管理员分页查询文章列表，支持按状态、分类和关键词筛选")
    @GetMapping
    public Result<PageResult<ArticleListVO>> getArticleList(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,

            @Parameter(description = "文章状态（1-已发布，2-草稿，3-已下架", example = "1")
            @RequestParam(required = false) Integer status,

            @Parameter(description = "文章分类ID", example = "1")
            @RequestParam(required = false) Long categoryId,

            @Parameter(description = "搜索关键词", example = "Vue")
            @RequestParam(required = false) String keyword
    ){
        log.info("管理员查询文章列表：page={}, pageSize={}, status={}, categoryId={}, keyword={}", page, pageSize, status, categoryId, keyword);
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPage(page);
        pageQuery.setPageSize(pageSize);

        PageResult<ArticleListVO> result = articleService.getAdminArticleList(pageQuery, status, categoryId, keyword);
        return Result.success(result);
    }

    /**
     * 根据ID获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @Operation(summary = "获取文章详情", description = "管理员根据ID获取文章详情")
    @GetMapping("/{id}")
    public Result<ArticleDetailVO> getArticleById(
            @Parameter(description = "文章ID", required = true)
            @PathVariable Long id
    ){
        log.info("管理员获取文章详情：id={}", id);
        ArticleDetailVO article = articleService.getAdminArticleById(id);
        return Result.success(article);
    }

    /**
     * 创建新文章
     *
     * @param articleDTO 文章数据
     * @return 创建的文章
     */
    @Operation(summary = "创建文章", description = "管理员创建文章")
    @PostMapping
    public Result<Article> createArticle(
            @Parameter(description = "文章数据", required = true)
            @Valid @RequestBody ArticleDTO articleDTO
    ) {
        log.info("管理员创建文章：title={}", articleDTO.getTitle());
        Article createdArticle = articleService.createArticle(articleDTO);
        return Result.success("文章创建成功", createdArticle);
    }

    /**
     * 更新文章
     *
     * @param id 文章ID
     * @param articleDTO 文章数据
     * @return 更新后的文章
     */
    @Operation(summary = "更新文章", description = "管理员更新文章信息")
    @PutMapping("/{id}")
    public Result<Article> updateArticle(
            @Parameter(description = "文章ID", required = true)
            @PathVariable Long id,

            @Parameter(description = "文章数据", required = true)
            @Valid @RequestBody ArticleDTO articleDTO
    ) {
        log.info("管理员更新文章：id={}, title={}", id, articleDTO.getTitle());
        Article updateArticle = articleService.updateArticle(articleDTO);
        return Result.success("文章更新成功", updateArticle);
    }

    /**
     * 删除文章
     *
     * @param ids 文章ID，多个用逗号分隔
     * @return 删除结果
     */
    @Operation(summary = "删除文章", description = "管理员删除文章，支持批量删除")
    @DeleteMapping
    public Result<String> deleteArticles(
            @Parameter(description = "文章ID，多个用逗号分隔", example = "1", required = true)
            @RequestParam("ids") String ids
    ) {
        log.info("管理员删除文章：ids={}", ids);

        return articleService.deleteArticles(ids);
    }

    /**
     * 切换文章置顶状态
     *
     * @param id 文章ID
     * @return 切换结果
     */
    @Operation(summary = "切换文章置顶状态", description = "管理员切换文章置顶状态")
    @PutMapping("/{id}/toggle-top")
    public Result<String> toggleArticleTop(
            @Parameter(description = "文章ID", example = "1", required = true)
            @PathVariable Long id
    ) {
        log.info("管理员切换文章置顶状态：id={}", id);
        return articleService.toggleArticleTop(id);
    }



    /**
     * 更新文章状态
     *
     * @param id 文章ID
     * @param status 文章状态（1-已发布，2-草稿，3-已下架）
     * @return 更新结果
     */
    @Operation(summary = "更新文章状态", description = "管理员更新文章状态")
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(
            @Parameter(description = "文章ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "文章状态（1-已发布，2-草稿，3-已下架）", example = "1", required = true)
            @RequestParam Integer status
    ){
        log.info("管理员更新文章状态：id={}, status={}", id, status);
        return articleService.updateStatus(id, status);
    }
}