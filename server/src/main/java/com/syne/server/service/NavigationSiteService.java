package com.syne.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.model.entity.NavigationSite;
import com.syne.server.model.dto.NavigationSiteDTO;
import com.syne.server.model.vo.NavigationCategoryWithSitesVO;
import com.syne.server.model.vo.NavigationSiteShowVO;
import com.syne.server.model.vo.NavigationSiteVO;
import com.syne.server.model.dto.SortOrderDTO;

import java.util.List;

/**
 * 导航站点Service接口
 */
public interface NavigationSiteService extends IService<NavigationSite> {

    /**
     * 分页查询导航站点列表
     */
    PageResult<NavigationSite> listNavigationSites(PageQuery pageQuery);

    /**
     * 根据分类ID查询导航站点列表
     */
    List<NavigationSite> listSitesByCategoryId(Long categoryId);

    /**
     * 查询所有导航站点
     */
    List<NavigationSite> listAllSites();

    /**
     * 根据ID获取导航站点详情
     */
    NavigationSite getNavigationSiteById(Long id);

    /**
     * 创建导航站点
     */
    NavigationSite createNavigationSite(NavigationSiteDTO dto);

    /**
     * 更新导航站点
     */
    NavigationSite updateNavigationSite(NavigationSiteDTO dto);

    /**
     * 删除导航站点
     */
    boolean deleteNavigationSite(Long id);

    /**
     * 批量删除导航站点
     */
    boolean batchDeleteNavigationSites(List<Long> ids);

    /**
     * 根据分类ID查询导航站点列表（包含分类名称）
     */
    List<NavigationSiteVO> listSiteVOsByCategoryId(Long categoryId);

    /**
     * 获取所有导航站点VO列表
     */
    List<NavigationSiteVO> listAllSiteVOs();

    /**
     * 用户端获取所有导航站点展示VO列表
     */
    List<NavigationSiteShowVO> listAllSiteShowVOs();

    /**
     * 管理端获取所有分类（含站点列表）
     */
    List<NavigationCategoryWithSitesVO> listAllCategoryWithSites();

    /**
     * 批量更新站点排序（支持跨分类拖拽）
     * @param orders 排序项列表（id, sortOrder, categoryId）
     * @return 是否更新成功
     */
    boolean batchUpdateSortOrder(List<SortOrderDTO.SortOrderItem> orders);
}