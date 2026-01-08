package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.entity.NavigationSite;
import com.syne.server.entity.vo.NavigationSiteShowVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 导航站点Mapper接口
 */
@Mapper
public interface NavigationSiteMapper extends BaseMapper<NavigationSite> {

    /**
     * 用户端获取所有导航站点展示VO列表
     */
    List<NavigationSiteShowVO> listAllSiteShowVOs();
}