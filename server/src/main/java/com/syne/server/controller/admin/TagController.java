package com.syne.server.controller.admin;

import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.model.entity.Tags;
import com.syne.server.model.dto.TagDTO;
import com.syne.server.model.vo.TagListVO;
import com.syne.server.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员标签控制器
 */
@Slf4j
@Tag(name = "管理员标签管理", description = "管理员标签相关接口")
@RestController
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
@Validated
public class TagController {

    private final TagService tagService;

    /**
     * 分页查询标签列表
     *
     * @param page       页码
     * @param pageSize   每页大小
     * @param keyword    搜索关键词
     * @param sortBy     排序字段
     * @param sortOrder  排序方向
     * @return 标签分页列表
     */
    @Operation(summary = "查询标签列表", description = "管理员分页查询标签列表，支持按关键词搜索和排序")
    @GetMapping
    public Result<PageResult<TagListVO>> getTagList(
        @Parameter(description = "页码", example = "1")
        @RequestParam(defaultValue = "1") Integer page,

        @Parameter(description = "每页大小", example = "10")
        @RequestParam(defaultValue = "10") Integer pageSize,

        @Parameter(description = "搜索关键词", example = "Vue")
        @RequestParam(required = false) String keyword,

        @Parameter(description = "排序字段", example = "usage_count")
        @RequestParam(required = false) String sortBy,

        @Parameter(description = "排序方向", example = "desc")
        @RequestParam(required = false) String sortOrder
    ) {
        log.info("分页查询标签列表：page={}, pageSize={}, keyword={}, sortBy={}, sortOrder={}",
                page, pageSize, keyword, sortBy, sortOrder);

        PageQuery pageQuery = new PageQuery();
        pageQuery.setPage(page);
        pageQuery.setPageSize(pageSize);

        PageResult<TagListVO> result = tagService.getTagList(pageQuery, keyword, sortBy, sortOrder);

        return Result.success(result);
    }

    /**
     * 根据ID获取标签详情
     *
     * @param id 标签ID
     * @return 标签详情
     */
    @Operation(summary = "获取标签详情", description = "管理员根据ID获取标签详情")
    @GetMapping("/{id}")
    public Result<Tags> getTagById(
        @Parameter(description = "标签ID", required = true)
        @PathVariable Long id
    ) {
        log.info("获取标签详情：id={}", id);
        Tags tag = tagService.getTagById(id);
        return Result.success(tag);
    }

    /**
     * 创建标签
     *
     * @param tagDTO 标签数据
     * @return 创建的标签
     */
    @Operation(summary = "创建标签", description = "管理员创建新标签")
    @PostMapping
    public Result<Tags> createTag(
        @Parameter(description = "标签数据", required = true)
        @Valid @RequestBody TagDTO tagDTO
    ) {
        log.info("创建标签：{}", tagDTO);
        Tags createdTags = tagService.createTag(tagDTO);
        return Result.success(createdTags);
    }

    /**
     * 更新标签
     *
     * @param tagDTO 标签数据
     * @param id     标签ID
     * @return 更新后的标签
     */
    @Operation(summary = "更新标签", description = "管理员更新标签信息")
    @PutMapping("/{id}")
    public Result<Tags> updateTag(
        @Parameter(description = "标签数据", required = true)
        @Valid @RequestBody TagDTO tagDTO,

        @Parameter(description = "标签ID", required = true)
        @PathVariable Long id
    ) {
        log.info("更新标签：id={}, {}", id, tagDTO);
        Tags updatedTags = tagService.updateTag(tagDTO, id);
        return Result.success(updatedTags);
    }

    /**
     * 删除标签
     *
     * @param ids 标签ID字符串
     * @return 删除结果
     */
    @Operation(summary = "删除标签", description = "删除单个或多个标签（逻辑删除），支持批量操作")
    @DeleteMapping
    public Result<String> deleteTags(
        @Parameter(description = "标签ID，单个或多个用英文逗号分隔", required = true, example = "1,2,3")
        @RequestParam String ids
    ) {
        log.info("删除标签：ids={}", ids);
        Result<String> result = tagService.deleteTags(ids);
        return result;
    }

}