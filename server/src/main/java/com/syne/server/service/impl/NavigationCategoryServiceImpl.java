package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.model.entity.NavigationCategory;
import com.syne.server.model.dto.NavigationCategoryDTO;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.NavigationCategoryMapper;
import com.syne.server.model.dto.SortOrderDTO;
import com.syne.server.service.NavigationCategoryService;
import com.syne.server.service.NavigationSiteService;
import com.syne.server.utils.NavigationCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 导航分类Service实现类
 */
@Service
@RequiredArgsConstructor
public class NavigationCategoryServiceImpl extends ServiceImpl<NavigationCategoryMapper, NavigationCategory> implements NavigationCategoryService {

    private final NavigationCategoryMapper navigationCategoryMapper;
    private final NavigationSiteService navigationSiteService;
    private final NavigationCacheManager navigationCacheManager;

    @Override
    public PageResult<NavigationCategory> listNavigationCategories(PageQuery pageQuery) {
        LambdaQueryWrapper<NavigationCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(NavigationCategory::getSortOrder).orderByDesc(NavigationCategory::getCreateTime);

        IPage<NavigationCategory> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
        IPage<NavigationCategory> resultPage = navigationCategoryMapper.selectPage(page, wrapper);

        return PageResult.<NavigationCategory>builder()
                .page(pageQuery.getPage())
                .pageSize(pageQuery.getPageSize())
                .total(resultPage.getTotal())
                .list(resultPage.getRecords())
                .build();
    }

    @Override
    public List<NavigationCategory> listAllCategories() {
        LambdaQueryWrapper<NavigationCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(NavigationCategory::getSortOrder).orderByDesc(NavigationCategory::getCreateTime);
        return navigationCategoryMapper.selectList(wrapper);
    }

    @Override
    public NavigationCategory getNavigationCategoryById(Long id) {
        return navigationCategoryMapper.selectById(id);
    }

    @Override
    @Transactional
    public NavigationCategory createNavigationCategory(NavigationCategoryDTO dto) {
        NavigationCategory category = new NavigationCategory();
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        navigationCategoryMapper.insert(category);
        navigationCacheManager.invalidateAll();
        return category;
    }

    @Override
    @Transactional
    public NavigationCategory updateNavigationCategory(NavigationCategoryDTO dto) {
        NavigationCategory category = navigationCategoryMapper.selectById(dto.getId());
        if (category == null) {
            throw new BusinessException("导航分类不存在");
        }

        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        navigationCategoryMapper.updateById(category);
        navigationCacheManager.invalidateAll();
        return category;
    }

    @Override
    @Transactional
    public boolean deleteNavigationCategory(Long id) {
        NavigationCategory category = navigationCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("导航分类不存在");
        }

        // 检查分类下是否还有站点
        if (hasSites(id)) {
            throw new BusinessException("该分类下还有导航站点，无法删除");
        }

        int result = navigationCategoryMapper.deleteById(id);
        if (result > 0) {
            navigationCacheManager.invalidateAll();
        }
        return result > 0;
    }

    @Override
    @Transactional
    public boolean batchDeleteNavigationCategories(List<Long> ids) {
        for (Long id : ids) {
            if (hasSites(id)) {
                throw new BusinessException("ID为" + id + "的分类下还有导航站点，无法删除");
            }
        }

        int result = navigationCategoryMapper.deleteBatchIds(ids);
        if (result > 0) {
            navigationCacheManager.invalidateAll();
        }
        return result > 0;
    }

    @Override
    public boolean hasSites(Long categoryId) {
        int count = navigationCategoryMapper.countSitesByCategoryId(categoryId);
        return count > 0;
    }

    @Override
    public long count() {
        return navigationCategoryMapper.selectCount(null);
    }

    @Override
    @Transactional
    public boolean batchUpdateSortOrder(List<SortOrderDTO.SortOrderItem> orders) {
        if (orders == null || orders.isEmpty()) {
            return true;
        }

        for (var item : orders) {
            NavigationCategory category = new NavigationCategory();
            category.setId(item.getId());
            category.setSortOrder(item.getSortOrder());
            navigationCategoryMapper.updateById(category);
        }

        navigationCacheManager.invalidateAll();
        return true;
    }
}
