package com.syne.server.controller.web;


import com.syne.server.common.Result;
import com.syne.server.entity.NavigationCategory;
import com.syne.server.entity.vo.NavigationSiteShowVO;
import com.syne.server.service.NavigationCategoryService;
import com.syne.server.service.NavigationSiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端导航控制器
 */
@Tag(name = "用户端导航", description = "用户端导航相关接口")
@RestController("webNavigationController")
@RequestMapping("/navigations")
@RequiredArgsConstructor
public class NavigationController {

    private final NavigationSiteService navigationSiteService;
    private final NavigationCategoryService navigationCategoryService;

    /**
     * 获取所有导航分类
     */
    @Operation(summary = "获取所有导航分类")
    @GetMapping("/categories")
    public Result<List<NavigationCategory>> getAllCategories() {
        List<NavigationCategory> categories = navigationCategoryService.listAllCategories();
        return Result.success(categories);
    }

    /**
     * 获取所有导航站点
     */
    @Operation(summary = "获取所有导航站点")
    @GetMapping()
    public Result<List<NavigationSiteShowVO>> getAllSites() {
        List<NavigationSiteShowVO> siteVOs = navigationSiteService.listAllSiteShowVOs();
        return Result.success(siteVOs);
    }
}