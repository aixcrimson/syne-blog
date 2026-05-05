package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.model.entity.Category;
import com.syne.server.model.dto.CategoryDTO;
import com.syne.server.model.vo.CategoryListVO;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.CategoryMapper;
import com.syne.server.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public PageResult<CategoryListVO> getCategoryList(PageQuery pageQuery) {
        // 查询文章列表
        List<CategoryListVO> list = categoryMapper.selectCategoryList(
                pageQuery.getOffset(),
                pageQuery.getPageSize()
        );

        // 查询总数
        Long total = categoryMapper.countCategories();

        // 构建分页结果返回
        return PageResult.build(
                pageQuery.getPage(),
                pageQuery.getPageSize(),
                total,
                list
        );
    }


    @Override
    public Category getById(Long id) {
        Category category = super.getById(id);
        if(category == null || category.getDeleted() == 1) {
            throw new BusinessException("分类不存在");
        }

        return category;
    }

    @Override
    @Transactional
    public Category createCategory(CategoryDTO categoryDTO){
        // 检查name是否被未删除的分类使用
        LambdaQueryWrapper<Category> nameQuery = new LambdaQueryWrapper<>();
        nameQuery.eq(Category::getName, categoryDTO.getName())
                .eq(Category::getDeleted, 0);
        if (this.count(nameQuery) > 0) {
            throw new BusinessException("分类名称已存在");
        }

        // 检查slug是否被未删除的分类使用
        LambdaQueryWrapper<Category> slugQuery = new LambdaQueryWrapper<>();
        slugQuery.eq(Category::getSlug, categoryDTO.getSlug())
                .eq(Category::getDeleted, 0);
        if (this.count(slugQuery) > 0) {
            throw new BusinessException("分类别名已存在");
        }

        // 检查是否存在已逻辑删除的同slug或同name记录（使用自定义SQL绕过@TableLogic）
        Category deletedCategory = categoryMapper.selectDeletedBySlug(categoryDTO.getSlug());
        if (deletedCategory == null) {
            deletedCategory = categoryMapper.selectDeletedByName(categoryDTO.getName());
        }

        if (deletedCategory != null) {
            // 恢复已删除的记录（使用自定义SQL绕过@TableLogic）
            categoryMapper.restoreDeletedCategory(
                    deletedCategory.getId(),
                    categoryDTO.getName(),
                    categoryDTO.getSlug(),
                    categoryDTO.getDescription(),
                    categoryDTO.getSortOrder()
            );
            Category restoredCategory = this.getById(deletedCategory.getId());
            log.info("恢复并更新已删除分类：{}", restoredCategory);
            return restoredCategory;
        }

        // 全新创建
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setSlug(categoryDTO.getSlug());
        category.setDescription(categoryDTO.getDescription());
        category.setSortOrder(categoryDTO.getSortOrder());
        this.save(category);

        log.info("创建分类：{}", category);
        return category;
    }

    @Override
    public Category updateCategory(CategoryDTO categoryDTO, Long id){
        // 获取分类
        Category category = this.getById(id);
        if(category == null || category.getDeleted() == 1) {
            throw new BusinessException("分类不存在");
        }

        category.setId(id);
        category.setName(categoryDTO.getName());
        category.setSlug(categoryDTO.getSlug());
        category.setDescription(categoryDTO.getDescription());
        category.setSortOrder(categoryDTO.getSortOrder());

        // 保存
        this.updateById(category);

        log.info("更新分类：{}", category);
        return this.getById(category.getId());
    }

    @Override
    @Transactional
    public Result<String> deleteCategories(String ids) {
        log.info("删除分类：ids={}", ids);
        if(StringUtils.isEmpty(ids)) {
            throw new BusinessException("分类ID不能为空");
        }

        String[] idArray = ids.split(",");
        if(idArray.length > 100) {
            throw new BusinessException("单次删除数量不能超过100个");
        }

        List<String> failedIds = new ArrayList<>();
        int successCount = 0;

        for(String idStr : idArray) {
            try {
                Long id = Long.parseLong(idStr.trim());
                this.deleteCategory(id);
                successCount++;
            } catch (NumberFormatException e) {
                failedIds.add(idStr + "(格式错误)");
                log.warn("无效的分类ID格式：{}", idStr);
            } catch (BusinessException e) {
                failedIds.add(idStr + "(" + e.getMessage() + ")");
                log.warn("删除分类失败：{}, 错误：{}", idStr, e.getMessage());
            }
        }

        log.info("批量删除分类成功：成功={}, 失败={}", successCount, failedIds.size());

        // 构建返回消息
        String message;
        if(failedIds.isEmpty()) {
            message = successCount == 1 ? "删除分类成功" :
                    String.format("成功删除 %d 个分类", successCount);
        } else {
            message = String.format("成功删除 %d 个分类，失败 %s",
                    successCount, String.join(", ", failedIds)
            );
        }

        return Result.success(message);
    }

    /**
     * 根据ID删除单个分类
     */
    public void deleteCategory(Long id) {
        // 检查分类是否存在
        Category category = this.getById(id);
        if(category == null || category.getDeleted() == 1) {
            throw new BusinessException("分类不存在");
        }

        // 逻辑删除
        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Category::getId, id)
                .set(Category::getDeleted, 1)
                .set(Category::getUpdateTime, LocalDateTime.now());

        this.update(updateWrapper);

        log.info("删除分类成功：id={}, name={}", id, category.getName());
    }

    @Override
    public List<CategoryListVO> getAllCategoryList(){
        // 查询文章列表
        List<CategoryListVO> list = categoryMapper.selectAllCategoryList();

        // 构建分页结果返回
        return list;
    }
}