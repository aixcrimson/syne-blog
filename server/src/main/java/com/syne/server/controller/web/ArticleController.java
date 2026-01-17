package com.syne.server.controller.web;

import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.model.vo.ArticleDetailVO;
import com.syne.server.model.vo.ArticleListVO;
import com.syne.server.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户端文章控制器
 */
@Slf4j
@Tag(name = "用户端文章管理", description = "用户端文章相关接口")
@RestController("webArticleController")
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

            @Parameter(description = "搜索关键词", example = "Vue")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "文章分类ID", example = "1")
            @RequestParam(required = false) Long categoryId,

            @Parameter(description = "文章标签ID（可以有多个，以逗号分隔）", example = "1,2,3")
            @RequestParam(required = false) String tagIds
    ){
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPage(page);
        pageQuery.setPageSize(pageSize);

        // 解析标签ID字符串为List
        List<Long> tagIdList = null;
        if (tagIds != null && !tagIds.trim().isEmpty()) {
            tagIdList = Arrays.stream(tagIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        }

        PageResult<ArticleListVO> result = articleService.getUserArticleList(pageQuery, keyword, categoryId, tagIdList);
        return Result.success(result);
    }

    /**
     * 根据ID获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @Operation(summary = "获取文章详情", description = "根据ID获取文章详情")
    @GetMapping("/{id}")
    public Result<ArticleDetailVO> getArticleById(
            @Parameter(description = "文章ID", required = true)
            @PathVariable Long id
    ){
        log.info("用户端获取文章详情：id={}", id);
        ArticleDetailVO article = articleService.getUserArticleById(id);
        return Result.success(article);
    }

    /**
     * 获取推荐文章列表
     *
     * @param limit 限制查询数量
     * @return 推荐文章列表
     */
    @Operation(summary = "获取推荐文章", description = "获取推荐文章列表")
    @GetMapping("/recommended")
    public Result<List<ArticleListVO>> getRecommended(
            @Parameter(description = "数量限制", example = "5")
            @RequestParam(defaultValue = "5") Integer limit
    ){
        log.info("用户端获取推荐文章列表：limit={}", limit);
        List<ArticleListVO> list = articleService.getRecommendedArticleList(limit);
        return Result.success(list);
    }

    /**
     * 获取用户点赞的文章列表
     *
     * @param page 页码
     * @param pageSize 每页大小
     * @return 用户点赞的文章列表
     */
    @Operation(summary = "获取用户点赞的文章列表", description = "获取用户点赞的文章列表")
    @GetMapping("/liked")
    public Result<PageResult<ArticleListVO>> getLikedArticleList(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize
    ){
        log.info("用户端获取用户点赞的文章列表：page={}, pageSize={}", page, pageSize);
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPage(page);
        pageQuery.setPageSize(pageSize);

        PageResult<ArticleListVO> result = articleService.getLikedArticleList(pageQuery);
        return Result.success(result);
    }

    /**
     * 获取用户收藏的文章列表
     *
     * @param page 页码
     * @param pageSize 每页大小
     * @return 用户收藏的文章列表
     */
    @Operation(summary = "获取用户收藏的文章列表", description = "获取用户收藏的文章列表")
    @GetMapping("/favorite")
    public Result<PageResult<ArticleListVO>> getFavoriteArticleList(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize
    ){
        log.info("用户端获取用户收藏的文章列表：page={}, pageSize={}", page, pageSize);
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPage(page);
        pageQuery.setPageSize(pageSize);

        PageResult<ArticleListVO> result = articleService.getFavoriteArticleList(pageQuery);
        return Result.success(result);
    }

    /**
     * 给文章点赞或取消点赞
     *
     * @param id 文章ID
     * @return 操作结果
     */
    @Operation(summary = "点赞文章", description = "给文章点赞或取消点赞")
    @PostMapping("/{id}/like")
    public Result<Map<String, Object>> toggleArticleLike(
            @Parameter(description = "文章ID", example = "1")
            @PathVariable Long id
    ){
        log.info("用户给文章点赞或取消点赞：id={}", id);
        return articleService.toggleArticleLike(id);
    }

    /**
     * 给文章收藏或取消收藏
     *
     * @param id 文章ID
     * @return 操作结果
     */
    @Operation(summary = "收藏文章", description = "给文章收藏或取消收藏")
    @PostMapping("/{id}/favorite")
    public Result<Map<String, Object>> toggleArticleFavorite(
            @Parameter(description = "文章ID", example = "1")
            @PathVariable Long id
    ){
        log.info("给文章收藏或取消收藏：id={}", id);
        return articleService.toggleArticleFavorite(id);
    }

    /**
     * 给文章增加浏览量
     *
     * @param id 文章ID
     * @return 操作结果
     */
    @Operation(summary = "文章增加浏览量", description = "给文章增加浏览量")
    @PostMapping("/{id}/views")
    public Result<Map<String, Object>> increaseViews(
            @Parameter(description = "文章ID", example = "1")
            @PathVariable Long id
    ){
        log.info("用户给文章增加浏览量：id={}", id);
        return articleService.increaseViews(id);
    }
}