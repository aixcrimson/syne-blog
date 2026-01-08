package com.syne.server.service;

import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.entity.NavigationSite;
import com.syne.server.entity.dto.NavigationSiteDTO;
import com.syne.server.entity.vo.NavigationSiteShowVO;
import com.syne.server.entity.vo.NavigationSiteVO;

import java.util.List;

/**
 * 导航站点Service接口
 */
public interface NavigationSiteService {

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
}