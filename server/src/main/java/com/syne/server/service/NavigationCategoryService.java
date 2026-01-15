package com.syne.server.service;

import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.entity.NavigationCategory;
import com.syne.server.entity.dto.NavigationCategoryDTO;

import java.util.List;

/**
 * 导航分类Service接口
 */
public interface NavigationCategoryService {

    /**
     * 分页查询导航分类列表
     */
    PageResult<NavigationCategory> listNavigationCategories(PageQuery pageQuery);

    /**
     * 查询所有导航分类
     */
    List<NavigationCategory> listAllCategories();

    /**
     * 根据ID获取导航分类详情
     */
    NavigationCategory getNavigationCategoryById(Long id);

    /**
     * 创建导航分类
     */
    NavigationCategory createNavigationCategory(NavigationCategoryDTO dto);

    /**
     * 更新导航分类
     */
    NavigationCategory updateNavigationCategory(NavigationCategoryDTO dto);

    /**
     * 删除导航分类
     */
    boolean deleteNavigationCategory(Long id);

    /**
     * 批量删除导航分类
     */
    boolean batchDeleteNavigationCategories(List<Long> ids);

    /**
     * 检查分类下是否有站点
     */
    boolean hasSites(Long categoryId);

    /**
     * 获取分类总数
     */
    long count();

    /**
     * 批量更新分类排序
     * @param orders 排序项列表（id, sortOrder）
     * @return 是否更新成功
     */
    boolean batchUpdateSortOrder(List<com.syne.server.entity.dto.SortOrderDTO.SortOrderItem> orders);
}