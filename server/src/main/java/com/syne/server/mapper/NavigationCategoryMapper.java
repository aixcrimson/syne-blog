package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.model.entity.NavigationCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 导航分类Mapper接口
 */
@Mapper
public interface NavigationCategoryMapper extends BaseMapper<NavigationCategory> {

    /**
     * 根据分类ID查询是否存在相关的导航站点
     */
    int countSitesByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 查询已逻辑删除的同name导航分类（绕过@TableLogic自动过滤）
     */
    @Select("SELECT * FROM navigation_categories WHERE name = #{name} AND deleted = 1 LIMIT 1")
    NavigationCategory selectDeletedByName(@Param("name") String name);

    /**
     * 恢复已逻辑删除的导航分类（绕过@TableLogic自动过滤）
     */
    @Update("UPDATE navigation_categories SET name = #{name}, sort_order = #{sortOrder}, deleted = 0, update_time = NOW() WHERE id = #{id}")
    int restoreDeletedCategory(@Param("id") Long id, @Param("name") String name, @Param("sortOrder") Integer sortOrder);
}