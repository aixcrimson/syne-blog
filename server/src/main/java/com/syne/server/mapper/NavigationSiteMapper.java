package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.entity.NavigationSite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 导航站点Mapper接口
 */
@Mapper
public interface NavigationSiteMapper extends BaseMapper<NavigationSite> {
}