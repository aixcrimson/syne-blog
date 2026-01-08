package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.entity.ArticleTag;
import com.syne.server.entity.Tags;
import com.syne.server.entity.dto.TagDTO;
import com.syne.server.entity.vo.TagListVO;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.ArticleTagMapper;
import com.syne.server.mapper.TagMapper;
import com.syne.server.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tags> implements TagService {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    @Override
    public PageResult<TagListVO> getTagList(PageQuery pageQuery, String keyword, String sortBy, String sortOrder) {
        // 查询标签列表
        List<TagListVO> list = tagMapper.selectTagList(
                keyword,
                StringUtils.hasText(sortBy) ? sortBy : "usage_count",
                StringUtils.hasText(sortOrder) ? sortOrder : "desc",
                pageQuery.getOffset(),
                pageQuery.getPageSize()
        );

        // 查询总数
        Long total = tagMapper.countTags(keyword);

        // 构建分页结果返回
        return PageResult.build(
                pageQuery.getPage(),
                pageQuery.getPageSize(),
                total,
                list
        );
    }

    @Override
    public Tags getTagById(Long id) {
        Tags tags = this.getById(id);
        if (tags == null || tags.getDeleted() == 1) {
            throw new BusinessException("标签不存在");
        }

        return tags;
    }

    @Override
    @Transactional
    public Tags createTag(TagDTO tagDTO) {
        // 检查标签名称是否已存在
        LambdaQueryWrapper<Tags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(com.syne.server.entity.Tags::getName, tagDTO.getName())
                .eq(com.syne.server.entity.Tags::getDeleted, 0);
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException("标签名称已存在");
        }

        // 检查slug是否已存在
        queryWrapper.clear();
        queryWrapper.eq(com.syne.server.entity.Tags::getSlug, tagDTO.getSlug())
                .eq(com.syne.server.entity.Tags::getDeleted, 0);
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException("标签别名已存在");
        }

        Tags tags = new Tags();
        BeanUtils.copyProperties(tagDTO, tags);
        if (!StringUtils.hasText(tags.getColor())) {
            tags.setColor("#409EFF");
        }
        tags.setUsageCount(0);

        // 保存
        this.save(tags);

        log.info("创建标签：{}", tags);
        return tags;
    }

    @Override
    @Transactional
    public Tags updateTag(TagDTO tagDTO, Long id) {
        // 获取标签
        Tags tags = this.getById(id);
        if (tags == null || tags.getDeleted() == 1) {
            throw new BusinessException("标签不存在");
        }

        // 检查标签名称是否被其他标签使用
        if (!tags.getName().equals(tagDTO.getName())) {
            LambdaQueryWrapper<Tags> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(com.syne.server.entity.Tags::getName, tagDTO.getName())
                    .eq(com.syne.server.entity.Tags::getDeleted, 0)
                    .ne(com.syne.server.entity.Tags::getId, id);
            if (this.count(queryWrapper) > 0) {
                throw new BusinessException("标签名称已存在");
            }
        }

        // 检查slug是否被其他标签使用
        if (!tags.getSlug().equals(tagDTO.getSlug())) {
            LambdaQueryWrapper<Tags> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(com.syne.server.entity.Tags::getSlug, tagDTO.getSlug())
                    .eq(com.syne.server.entity.Tags::getDeleted, 0)
                    .ne(com.syne.server.entity.Tags::getId, id);
            if (this.count(queryWrapper) > 0) {
                throw new BusinessException("标签别名已存在");
            }
        }

        // 更新标签信息
        BeanUtils.copyProperties(tagDTO, tags);
        tags.setId(id);

        // 保存
        this.updateById(tags);

        log.info("更新标签：{}", tags);
        return this.getById(tags.getId());
    }

    @Override
    @Transactional
    public Result<String> deleteTags(String ids) {
        log.info("删除标签：ids={}", ids);
        if (!StringUtils.hasText(ids)) {
            throw new BusinessException("标签ID不能为空");
        }

        String[] idArray = ids.split(",");
        if (idArray.length > 100) {
            throw new BusinessException("单次删除数量不能超过100个");
        }

        List<String> failedIds = new ArrayList<>();
        int successCount = 0;

        for (String idStr : idArray) {
            try {
                Long id = Long.parseLong(idStr.trim());
                this.deleteTag(id);
                successCount++;
            } catch (NumberFormatException e) {
                failedIds.add(idStr + "(格式错误)");
                log.warn("无效的标签ID格式：{}", idStr);
            } catch (BusinessException e) {
                failedIds.add(idStr + "(" + e.getMessage() + ")");
                log.warn("删除标签失败：id={}, 原因={}", idStr, e.getMessage());
            }
        }

        // 构建返回消息
        String message;
        if (failedIds.isEmpty()) {
            message = successCount == 1 ? "标签删除成功" : "成功删除 " + successCount + " 个标签";
            return Result.success(message);
        } else {
            message = "成功删除 " + successCount + " 个标签，失败：" + String.join(", ", failedIds);
            return Result.success(message);
        }
    }

    /**
     * 删除单个标签
     */
    private void deleteTag(Long id) {
        Tags tags = this.getById(id);
        if (tags == null || tags.getDeleted() == 1) {
            throw new BusinessException("标签不存在");
        }

        // 检查标签是否被文章使用
        Integer usageCount = tagMapper.countArticleTagsByTagId(id);
        if (usageCount > 0) {
            throw new BusinessException("标签'" + tags.getName() + "'正在被" + usageCount + "篇文章使用，无法删除");
        }

        // 逻辑删除
        this.removeById(id);
    }

    @Override
    public List<Tags> getTagsByArticleId(Long articleId) {
        if (articleId == null) {
            return new ArrayList<>();
        }

        // 获取文章关联的标签ID列表
        List<Long> tagIds = articleTagMapper.selectTagIdsByArticleId(articleId);
        if (tagIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据标签ID批量查询标签信息
        LambdaQueryWrapper<Tags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Tags::getId, tagIds)
                .eq(Tags::getDeleted, 0)
                .orderByAsc(Tags::getId); // 按ID排序保持一致性

        return this.list(queryWrapper);
    }

    @Override
    public List<TagListVO> getAllTagList(){
        // 查询文章列表
        List<TagListVO> list = new ArrayList<>();

        List<Tags> tags = tagMapper.selectList(null);

        // 构建分页结果返回
        return list;
    }
}