package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.entity.Category;
import com.syne.server.entity.vo.CategoryListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}