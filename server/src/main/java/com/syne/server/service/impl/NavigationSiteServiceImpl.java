package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.entity.NavigationCategory;
import com.syne.server.entity.NavigationSite;
import com.syne.server.entity.dto.NavigationSiteDTO;
import com.syne.server.entity.vo.NavigationSiteShowVO;
import com.syne.server.entity.vo.NavigationSiteVO;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.NavigationCategoryMapper;
import com.syne.server.mapper.NavigationSiteMapper;
import com.syne.server.service.NavigationSiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 导航站点Service实现类
 */
@Service
@RequiredArgsConstructor
public class NavigationSiteServiceImpl extends ServiceImpl<NavigationSiteMapper, NavigationSite> implements NavigationSiteService {

    private final NavigationSiteMapper navigationSiteMapper;
    private final NavigationCategoryMapper navigationCategoryMapper;

    @Override
    public PageResult<NavigationSite> listNavigationSites(PageQuery pageQuery) {
        LambdaQueryWrapper<NavigationSite> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(NavigationSite::getSortOrder).orderByDesc(NavigationSite::getCreateTime);

        IPage<NavigationSite> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
        IPage<NavigationSite> resultPage = navigationSiteMapper.selectPage(page, wrapper);

        return PageResult.<NavigationSite>builder()
                .page(pageQuery.getPage())
                .pageSize(pageQuery.getPageSize())
                .total(resultPage.getTotal())
                .list(resultPage.getRecords())
                .build();
    }

    @Override
    public List<NavigationSite> listSitesByCategoryId(Long categoryId) {
        LambdaQueryWrapper<NavigationSite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NavigationSite::getCategoryId, categoryId);
        wrapper.orderByDesc(NavigationSite::getSortOrder).orderByDesc(NavigationSite::getCreateTime);
        return navigationSiteMapper.selectList(wrapper);
    }

    @Override
    public List<NavigationSite> listAllSites() {
        LambdaQueryWrapper<NavigationSite> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(NavigationSite::getSortOrder).orderByDesc(NavigationSite::getCreateTime);
        return navigationSiteMapper.selectList(wrapper);
    }

    @Override
    public NavigationSite getNavigationSiteById(Long id) {
        return navigationSiteMapper.selectById(id);
    }

    @Override
    @Transactional
    public NavigationSite createNavigationSite(NavigationSiteDTO dto) {
        // 检查分类是否存在
        if (navigationCategoryMapper.selectById(dto.getCategoryId()) == null) {
            throw new BusinessException("分类不存在");
        }

        NavigationSite site = new NavigationSite();
        site.setCategoryId(dto.getCategoryId());
        site.setName(dto.getName());
        site.setDescription(dto.getDescription());
        site.setUrl(dto.getUrl());
        site.setIcon(dto.getIcon());
        site.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        navigationSiteMapper.insert(site);
        return site;
    }

    @Override
    @Transactional
    public NavigationSite updateNavigationSite(NavigationSiteDTO dto) {
        NavigationSite site = navigationSiteMapper.selectById(dto.getId());
        if (site == null) {
            throw new BusinessException("导航站点不存在");
        }

        // 检查分类是否存在
        if (navigationCategoryMapper.selectById(dto.getCategoryId()) == null) {
            throw new BusinessException("分类不存在");
        }

        site.setCategoryId(dto.getCategoryId());
        site.setName(dto.getName());
        site.setDescription(dto.getDescription());
        site.setUrl(dto.getUrl());
        site.setIcon(dto.getIcon());
        site.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        navigationSiteMapper.updateById(site);
        return site;
    }

    @Override
    @Transactional
    public boolean deleteNavigationSite(Long id) {
        NavigationSite site = navigationSiteMapper.selectById(id);
        if (site == null) {
            throw new BusinessException("导航站点不存在");
        }

        int result = navigationSiteMapper.deleteById(id);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean batchDeleteNavigationSites(List<Long> ids) {
        int result = navigationSiteMapper.deleteBatchIds(ids);
        return result > 0;
    }

    @Override
    public List<NavigationSiteVO> listSiteVOsByCategoryId(Long categoryId) {
        List<NavigationSite> sites = listSitesByCategoryId(categoryId);

        // 获取分类信息
        NavigationCategory category = navigationCategoryMapper.selectById(categoryId);
        String categoryName = category != null ? category.getName() : "";

        return sites.stream().map(site -> {
            NavigationSiteVO vo = new NavigationSiteVO();
            vo.setId(site.getId());
            vo.setCategoryId(site.getCategoryId());
            vo.setCategoryName(categoryName);
            vo.setName(site.getName());
            vo.setDescription(site.getDescription());
            vo.setUrl(site.getUrl());
            vo.setIcon(site.getIcon());
            vo.setSortOrder(site.getSortOrder());
            vo.setCreateTime(site.getCreateTime());
            vo.setUpdateTime(site.getUpdateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<NavigationSiteVO> listAllSiteVOs() {
        List<NavigationSite> allSites = listAllSites();

        // 获取所有分类ID
        List<Long> categoryIds = allSites.stream()
            .map(NavigationSite::getCategoryId)
            .distinct()
            .collect(Collectors.toList());

        // 批量获取分类信息
        Map<Long, String> categoryMap = navigationCategoryMapper
            .selectBatchIds(categoryIds)
            .stream()
            .collect(Collectors.toMap(NavigationCategory::getId, NavigationCategory::getName));

        return allSites.stream().map(site -> {
            NavigationSiteVO vo = new NavigationSiteVO();
            vo.setId(site.getId());
            vo.setCategoryId(site.getCategoryId());
            vo.setCategoryName(categoryMap.getOrDefault(site.getCategoryId(), ""));
            vo.setName(site.getName());
            vo.setDescription(site.getDescription());
            vo.setUrl(site.getUrl());
            vo.setIcon(site.getIcon());
            vo.setSortOrder(site.getSortOrder());
            vo.setCreateTime(site.getCreateTime());
            vo.setUpdateTime(site.getUpdateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<NavigationSiteShowVO> listAllSiteShowVOs(){
        return navigationSiteMapper.listAllSiteShowVOs();
    }
}