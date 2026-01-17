package com.syne.server.controller.web;

/**
 * 用户端分类控制器
 */

import com.syne.server.common.Result;
import com.syne.server.model.vo.CategoryListVO;
import com.syne.server.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "用户端分类管理", description = "用户端分类相关接口")
@RestController("webCategoryController")
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController{

private final CategoryService categoryService;

/**
 * 查询所有分类列表
 *
 * @return 分类列表
 */
@Operation(summary = "查询分类列表", description = "用户端查询分类列表")
@GetMapping
public Result<List<CategoryListVO>> getAllCategoryList() {
    log.info("查询分类列表");
    List < CategoryListVO > result = categoryService.getAllCategoryList();

    return Result.success(result);
}
}