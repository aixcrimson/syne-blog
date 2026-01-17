package com.syne.server.controller.admin;

import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.model.entity.NavigationCategory;
import com.syne.server.model.entity.NavigationSite;
import com.syne.server.model.dto.BookmarkMappingDTO;
import com.syne.server.model.dto.BookmarkPreviewDTO;
import com.syne.server.model.dto.NavigationCategoryDTO;
import com.syne.server.model.dto.NavigationSiteDTO;
import com.syne.server.model.vo.NavigationCategoryWithSitesVO;
import com.syne.server.model.vo.NavigationSiteVO;
import com.syne.server.model.dto.SortOrderDTO;
import com.syne.server.service.BookmarkImportService;
import com.syne.server.service.NavigationCategoryService;
import com.syne.server.service.NavigationSiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 管理员端导航管理接口
 */
@Tag(name = "管理员-导航管理", description = "管理员端导航分类和导航站点管理接口")
@RestController
@RequestMapping("/admin/navigation")
@RequiredArgsConstructor
public class NavigationController {

    private final NavigationCategoryService navigationCategoryService;
    private final NavigationSiteService navigationSiteService;
    private final BookmarkImportService bookmarkImportService;

    // ========== 导航分类相关接口 ==========

    /**
     * 分页查询导航分类列表
     */
    @Operation(summary = "分页查询导航分类列表")
    @GetMapping("/categories")
    public Result<PageResult<NavigationCategory>> getNavigationCategoryList(PageQuery pageQuery) {
        PageResult<NavigationCategory> result = navigationCategoryService.listNavigationCategories(pageQuery);
        return Result.success(result);
    }

    /**
     * 获取所有导航分类
     */
    @Operation(summary = "获取所有导航分类")
    @GetMapping("/categories/all")
    public Result<List<NavigationCategory>> getAllNavigationCategories() {
        List<NavigationCategory> categories = navigationCategoryService.listAllCategories();
        return Result.success(categories);
    }

    /**
     * 根据ID获取导航分类详情
     */
    @Operation(summary = "获取导航分类详情")
    @GetMapping("/categories/{id}")
    public Result<NavigationCategory> getNavigationCategory(@PathVariable Long id) {
        NavigationCategory category = navigationCategoryService.getNavigationCategoryById(id);
        if (category == null) {
            return Result.error("导航分类不存在");
        }
        return Result.success(category);
    }

    /**
     * 创建导航分类
     */
    @Operation(summary = "创建导航分类")
    @PostMapping("/categories")
    public Result<NavigationCategory> createNavigationCategory(@Valid @RequestBody NavigationCategoryDTO dto) {
        NavigationCategory category = navigationCategoryService.createNavigationCategory(dto);
        return Result.success(category);
    }

    /**
     * 更新导航分类
     */
    @Operation(summary = "更新导航分类")
    @PutMapping("/categories")
    public Result<NavigationCategory> updateNavigationCategory(@Valid @RequestBody NavigationCategoryDTO dto) {
        NavigationCategory category = navigationCategoryService.updateNavigationCategory(dto);
        return Result.success(category);
    }

    /**
     * 删除导航分类
     */
    @Operation(summary = "删除导航分类")
    @DeleteMapping("/categories/{id}")
    public Result<String> deleteNavigationCategory(@PathVariable Long id) {
        boolean result = navigationCategoryService.deleteNavigationCategory(id);
        if (result) {
            return Result.success("导航分类删除成功");
        } else {
            return Result.error("导航分类删除失败");
        }
    }

    /**
     * 批量删除导航分类
     */
    @Operation(summary = "批量删除导航分类")
    @DeleteMapping("/categories")
    public Result<String> batchDeleteNavigationCategories(@RequestParam String ids) {
        if (ids == null || ids.trim().isEmpty()) {
            return Result.error("请选择要删除的分类");
        }

        String[] idArray = ids.split(",");
        if (idArray.length > 100) {
            return Result.error("单次批量删除数量不能超过100个");
        }

        List<Long> idList = new java.util.ArrayList<>();
        for (String idStr : idArray) {
            try {
                idList.add(Long.parseLong(idStr.trim()));
            } catch (NumberFormatException e) {
                return Result.error("ID格式错误：" + idStr);
            }
        }

        boolean result = navigationCategoryService.batchDeleteNavigationCategories(idList);
        if (result) {
            return Result.success("成功删除 " + idList.size() + " 个导航分类");
        } else {
            return Result.error("批量删除失败");
        }
    }

    /**
     * 批量更新分类排序
     */
    @Operation(summary = "批量更新分类排序")
    @PutMapping("/categories/sort")
    public Result<String> updateCategorySortOrder(@Valid @RequestBody SortOrderDTO dto) {
        boolean result = navigationCategoryService.batchUpdateSortOrder(dto.getOrders());
        if (result) {
            return Result.success("排序更新成功");
        } else {
            return Result.error("排序更新失败");
        }
    }

    // ========== 导航站点相关接口 ==========

    /**
     * 分页查询导航站点列表
     */
    @Operation(summary = "分页查询导航站点列表")
    @GetMapping("/sites")
    public Result<PageResult<NavigationSite>> getNavigationSiteList(PageQuery pageQuery) {
        PageResult<NavigationSite> result = navigationSiteService.listNavigationSites(pageQuery);
        return Result.success(result);
    }

    /**
     * 根据分类ID查询导航站点列表
     */
    @Operation(summary = "根据分类ID查询导航站点列表")
    @GetMapping("/categories/{categoryId}/sites")
    public Result<List<NavigationSiteVO>> getSitesByCategoryId(@PathVariable Long categoryId) {
        List<NavigationSiteVO> siteVOs = navigationSiteService.listSiteVOsByCategoryId(categoryId);
        return Result.success(siteVOs);
    }

    /**
     * 获取所有导航站点（按分类分组）
     */
    @Operation(summary = "获取所有导航站点（按分类分组）")
    @GetMapping("/sites/all")
    public Result<List<NavigationCategoryWithSitesVO>> getAllNavigationSites() {
        List<NavigationCategoryWithSitesVO> categoryWithSites = navigationSiteService.listAllCategoryWithSites();
        return Result.success(categoryWithSites);
    }

    /**
     * 根据ID获取导航站点详情
     */
    @Operation(summary = "获取导航站点详情")
    @GetMapping("/sites/{id}")
    public Result<NavigationSite> getNavigationSite(@PathVariable Long id) {
        NavigationSite site = navigationSiteService.getNavigationSiteById(id);
        if (site == null) {
            return Result.error("导航站点不存在");
        }
        return Result.success(site);
    }

    /**
     * 创建导航站点
     */
    @Operation(summary = "创建导航站点")
    @PostMapping("/sites")
    public Result<NavigationSite> createNavigationSite(@Valid @RequestBody NavigationSiteDTO dto) {
        NavigationSite site = navigationSiteService.createNavigationSite(dto);
        return Result.success(site);
    }

    /**
     * 更新导航站点
     */
    @Operation(summary = "更新导航站点")
    @PutMapping("/sites")
    public Result<NavigationSite> updateNavigationSite(@Valid @RequestBody NavigationSiteDTO dto) {
        NavigationSite site = navigationSiteService.updateNavigationSite(dto);
        return Result.success(site);
    }

    /**
     * 删除导航站点
     */
    @Operation(summary = "删除导航站点")
    @DeleteMapping("/sites/{id}")
    public Result<String> deleteNavigationSite(@PathVariable Long id) {
        boolean result = navigationSiteService.deleteNavigationSite(id);
        if (result) {
            return Result.success("导航站点删除成功");
        } else {
            return Result.error("导航站点删除失败");
        }
    }

    /**
     * 批量删除导航站点
     */
    @Operation(summary = "批量删除导航站点")
    @DeleteMapping("/sites")
    public Result<String> batchDeleteNavigationSites(@RequestParam String ids) {
        if (ids == null || ids.trim().isEmpty()) {
            return Result.error("请选择要删除的站点");
        }

        String[] idArray = ids.split(",");
        if (idArray.length > 100) {
            return Result.error("单次批量删除数量不能超过100个");
        }

        List<Long> idList = new java.util.ArrayList<>();
        for (String idStr : idArray) {
            try {
                idList.add(Long.parseLong(idStr.trim()));
            } catch (NumberFormatException e) {
                return Result.error("ID格式错误：" + idStr);
            }
        }

        boolean result = navigationSiteService.batchDeleteNavigationSites(idList);
        if (result) {
            return Result.success("成功删除 " + idList.size() + " 个导航站点");
        } else {
            return Result.error("批量删除失败");
        }
    }

    /**
     * 批量更新站点排序（支持跨分类拖拽）
     */
    @Operation(summary = "批量更新站点排序")
    @PutMapping("/sites/sort")
    public Result<String> updateSiteSortOrder(@Valid @RequestBody SortOrderDTO dto) {
        boolean result = navigationSiteService.batchUpdateSortOrder(dto.getOrders());
        if (result) {
            return Result.success("排序更新成功");
        } else {
            return Result.error("排序更新失败");
        }
    }

    // ========== 书签导入相关接口 ==========

    /**
     * 解析书签文件
     */
    @Operation(summary = "解析Chrome书签文件")
    @PostMapping("/bookmarks/parse")
    public Result<BookmarkPreviewDTO> parseBookmarkFile(@RequestParam("file") MultipartFile file) {
        try {
            BookmarkPreviewDTO preview = bookmarkImportService.parseBookmarkFile(file);
            return Result.success(preview);
        } catch (Exception e) {
            return Result.error("解析失败：" + e.getMessage());
        }
    }

    /**
     * 导入书签数据
     */
    @Operation(summary = "导入书签数据")
    @PostMapping("/bookmarks/import")
    public Result<String> importBookmarks(@Valid @RequestBody BookmarkMappingDTO mappingDTO) {
        try {
            BookmarkImportService.ImportResult result = bookmarkImportService.importBookmarks(mappingDTO);

            if (result.hasErrors()) {
                return Result.error(result.getErrorMessage());
            }

            String message = String.format("导入完成：成功 %d 个，跳过 %d 个，失败 %d 个",
                    result.getSuccessCount(), result.getSkipCount(), result.getErrorCount());

            return Result.success(message);
        } catch (Exception e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }
}