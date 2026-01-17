package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.model.entity.NavigationCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 导航分类Mapper接口
 */
@Mapper
public interface NavigationCategoryMapper extends BaseMapper<NavigationCategory> {

    /**
     * 根据分类ID查询是否存在相关的导航站点
     */
    int countSitesByCategoryId(@Param("categoryId") Long categoryId);
}