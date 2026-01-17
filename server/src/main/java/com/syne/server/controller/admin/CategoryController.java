package com.syne.server.controller.admin;

import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.model.entity.Category;
import com.syne.server.model.dto.CategoryDTO;
import com.syne.server.model.vo.CategoryListVO;
import com.syne.server.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员分类控制器
 */
@Slf4j
@Tag(name = "管理员分类管理", description = "管理员分类相关接口")
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 分页查询分类列表
     *
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分类分页列表
     */
    @Operation(summary = "查询分类列表", description = "管理员分页查询分类列表，按排序权重降序排序")
    @GetMapping
    public Result<PageResult<CategoryListVO>> getCategoryList(
        @Parameter(description = "页码", example = "1")
        @RequestParam(defaultValue = "1") Integer page,

        @Parameter(description = "每页大小", example = "10")
        @RequestParam(defaultValue = "10") Integer pageSize
    ){
        log.info("分页查询分类列表：page={}, pageSize={}", page, pageSize);
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPage(page);
        pageQuery.setPageSize(pageSize);

        PageResult<CategoryListVO> result = categoryService.getCategoryList(pageQuery);

        return Result.success(result);
    }

    /**
     * 根据ID获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    @Operation(summary = "获取分类详情", description = "管理员根据ID获取分类详情")
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(
        @Parameter(description = "分类ID", required = true)
        @PathVariable Long id
    ){
        log.info("获取分类详情：id={}", id);
        Category category = categoryService.getById(id);

        return Result.success(category);
    }

    /**
     * 创建分类
     *
     * @param categoryDTO 分类数据
     * @return 创建的分类
     */
    @Operation(summary = "创建分类", description = "管理员创建分类")
    @PostMapping
    public Result<Category> createCategory(
        @Parameter(description = "分类数据", required = true)
        @Valid @RequestBody CategoryDTO categoryDTO
    ){
        log.info("创建分类：{}", categoryDTO);
        Category createdCategory = categoryService.createCategory(categoryDTO);
        return Result.success(createdCategory);
    }

    /**
     * 更新分类
     *
     * @param categoryDTO 分类数据
     * @param id 分类ID
     * @return 更新后的分类
     */
    @Operation(summary = "更新分类", description = "管理员更新分类")
    @PutMapping("/{id}")
    public Result<Category> updateCategory(
        @Parameter(description = "分类数据", required = true)
        @Valid @RequestBody CategoryDTO categoryDTO,

        @Parameter(description = "分类ID", required = true)
        @PathVariable Long id
    ){
        log.info("更新分类：{}", categoryDTO);
        Category updatedCategory = categoryService.updateCategory(categoryDTO, id);
        return Result.success(updatedCategory);
    }

    /**
     * 删除分类
     *
     * @param ids 分类ID，多个用逗号分隔
     * @return 删除结果
     */
    @Operation(summary = "删除分类", description = "管理员删除分类")
    @DeleteMapping
    public Result<String> deleteCategories(
        @Parameter(description = "分类ID", required = true)
        @RequestParam("ids") String ids
    ){ 
        log.info("删除分类：ids={}", ids);
        return categoryService.deleteCategories(ids);
    }
}