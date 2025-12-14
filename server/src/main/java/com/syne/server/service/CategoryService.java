package com.syne.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.entity.Category;
import com.syne.server.entity.dto.CategoryDTO;
import com.syne.server.entity.vo.CategoryListVO;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {

    /**
     * 分页查询分类列表
     *
     * @param pageQuery 分页参数，包括页码和每页大小
     * @return 分类分页列表
     */
    PageResult<CategoryListVO> getCategoryList(PageQuery pageQuery);

    /**
     * 根据id查询分类
     * 
     * @param id 分类ID
     * @return 分类信息
     */
    Category getById(Long id);

    /**
     * 创建分类
     *
     * @param categoryDTO 分类数据
     * @return 分类信息
     */
    Category createCategory(CategoryDTO categoryDTO);

    /**
     * 更新分类
     *
     * @param categoryDTO 分类数据
     * @param id 分类ID
     * @return 更新后的分类信息
     */
    Category updateCategory(CategoryDTO categoryDTO, Long id);

    /**
     * 删除分类
     *
     * @param ids 分类ID
     * @return 删除结果
     */
    Result<String> deleteCategories(String ids);

}