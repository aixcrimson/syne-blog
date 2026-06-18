package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.model.entity.Category;
import com.syne.server.model.vo.CategoryDistributionVO;
import com.syne.server.model.vo.CategoryListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 分类Mapper接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 查询分类列表
     *
     * @param offset      分页偏移量
     * @param pageSize    分页大小
     * @return 分类列表
     */
    List<CategoryListVO> selectCategoryList(
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );

    /**
     * 查询分类数量
     *
     * @return 分类数量
     */
    Long countCategories();

    /**
     * 查询所有分类列表
     *
     * @return 分类列表
     */
    List<CategoryListVO> selectAllCategoryList();

    /**
     * 查询已逻辑删除的同slug分类（绕过@TableLogic自动过滤）
     */
    @Select("SELECT * FROM categories WHERE slug = #{slug} AND deleted = 1 LIMIT 1")
    Category selectDeletedBySlug(@Param("slug") String slug);

    /**
     * 查询已逻辑删除的同name分类（绕过@TableLogic自动过滤）
     */
    @Select("SELECT * FROM categories WHERE name = #{name} AND deleted = 1 LIMIT 1")
    Category selectDeletedByName(@Param("name") String name);

    /**
     * 恢复已逻辑删除的分类（绕过@TableLogic自动过滤）
     */
    @Update("UPDATE categories SET name = #{name}, slug = #{slug}, description = #{description}, sort_order = #{sortOrder}, deleted = 0, update_time = NOW() WHERE id = #{id}")
    int restoreDeletedCategory(@Param("id") Long id, @Param("name") String name, @Param("slug") String slug, @Param("description") String description, @Param("sortOrder") Integer sortOrder);

    // ==================== 仪表盘图表查询 ====================

    /**
     * 查询各分类的已发布文章数分布
     *
     * @return 分类文章分布列表
     */
    List<CategoryDistributionVO> selectCategoryArticleDistribution();
}