package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.entity.Article;
import com.syne.server.entity.dto.ArticleDTO;
import com.syne.server.entity.vo.ArticleListVO;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.ArticleMapper;
import com.syne.server.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final ArticleMapper articleMapper;

    @Override
    public PageResult<ArticleListVO> getArticleList(PageQuery pageQuery, Integer status) {
        // 查询文章列表
        List<ArticleListVO> list = articleMapper.selectArticleList(
                pageQuery.getOffset(),
                pageQuery.getPageSize(),
                status
        );

        // 处理标签字段：将都好分隔得字符串转为 List
        list.forEach(article -> {
            if(article.getTags() != null && !article.getTags().isEmpty()) {
                String tagsStr = article.getTags().get(0);
                if(tagsStr != null && !tagsStr.isEmpty()) {
                    article.setTags(Arrays.asList(tagsStr.split(",")));
                }
            }
        });

        // 查询总数
        Long total = articleMapper.countArticles(status);

        // 构建分页结果
        return PageResult.build(
                pageQuery.getPage(),
                pageQuery.getPageSize(),
                total,
                list
        );
    }

    @Override
    public PageResult<ArticleListVO> getAdminArticleList(PageQuery pageQuery, Integer status, String keyword) {
        // 查询文章列表
        List<ArticleListVO> list = articleMapper.selectAdminArticleList(
                pageQuery.getOffset(),
                pageQuery.getPageSize(),
                status,
                keyword
        );

        // 处理标签字段：将逗号分隔的字符串转为 List
        list.forEach(article -> {
            if(article.getTags() != null && !article.getTags().isEmpty()) {
                String tagsStr = article.getTags().get(0);
                if(tagsStr != null && !tagsStr.isEmpty()) {
                    article.setTags(Arrays.asList(tagsStr.split(",")));
                }
            }
        });

        // 查询总数
        Long total = articleMapper.countAdminArticles(status, keyword);

        // 构建分页结果
        return PageResult.build(
            pageQuery.getPage(),
            pageQuery.getPageSize(),
            total,
            list
        );
    }

    @Override
    public Article getAdminArticleById(Long id) {
        Article article = this.getById(id);
        if(article == null || article.getDeleted() == 1) {
            throw new BusinessException("文章不存在");
        }
        return article;
    }

    @Override
    public Article createArticle(ArticleDTO articleDTO) {
        // 1.转换DTO为实体
        Article article = new Article();
        article.setCategoryId(articleDTO.getCategoryId());
        article.setTitle(articleDTO.getTitle());
        article.setSummary(articleDTO.getSummary());
        article.setContent(articleDTO.getContent());
        article.setCoverImage(articleDTO.getCoverImage());
        article.setStatus(articleDTO.getStatus());
        article.setIsTop(articleDTO.getIsTop() != null ? articleDTO.getIsTop() : 0); // 默认不置顶
        article.setIsRecommend(articleDTO.getIsRecommend() != null ? articleDTO.getIsRecommend() : 0); // 默认不推荐

        // 2.设置默认值
        article.setDeleted(0);
        article.setViews(0);
        article.setLikes(0);
        article.setFavorites(0);
        article.setCommentsCount(0);

        // 如果状态为发布，设置发布时间
        if(article.getStatus() != null && article.getStatus() == 1) {
            article.setPublishedTime(LocalDateTime.now());
        }

        // 3.保存
        this.save(article);

        // TODO 4.处理标签关联

        log.info("创建文章成功：id={}, title={}", article.getId(), article.getTitle());
        return article;
    }

    @Override
    public Article updateArticle(ArticleDTO articleDTO) {
        // 1.检查文章是否存在
        Article existingArticle = this.getById(articleDTO.getId());
        if(existingArticle == null || existingArticle.getDeleted() == 1) {
            throw new BusinessException("文章不存在");
        }

        // 2.更新文章
        Article article = new Article();
        article.setId(articleDTO.getId());
        article.setCategoryId(articleDTO.getCategoryId());
        article.setTitle(articleDTO.getTitle());
        article.setSummary(articleDTO.getSummary());
        article.setContent(articleDTO.getContent());
        article.setCoverImage(articleDTO.getCoverImage());
        article.setStatus(articleDTO.getStatus());
        article.setIsTop(articleDTO.getIsTop() != null ? articleDTO.getIsTop() : 0); // 默认不置顶
        article.setIsRecommend(articleDTO.getIsRecommend() != null ? articleDTO.getIsRecommend() : 0); // 默认不推荐


        // 如果状态从未发布改为发布，设置发布时间
        if(article.getStatus() != null && article.getStatus() == 1
                && (existingArticle.getStatus() != null && existingArticle.getStatus() != 1)
            ) {
            article.setPublishedTime(LocalDateTime.now());
        }

        this.updateById(article);

        // TODO 4.处理标签关联

        log.info("更新文章成功：id={}, title={}", article.getId(), article.getTitle());
        return this.getById(article.getId());
    }

    @Override
    @Transactional
    public Result<String> deleteArticles(String ids) {
        // 1.参数验证
        if(!StringUtils.hasText(ids)) {
            throw new BusinessException("文章ID不能为空");
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
                this.deleteArticle(id);
                successCount++;
            } catch (NumberFormatException e) {
                failedIds.add(idStr + "(格式错误)");
                log.warn("无效的文章ID格式：{}", idStr);
            } catch (BusinessException e) {
                failedIds.add(idStr + "(" + e.getMessage() + ")");
                log.warn("删除文章失败：{}, 错误：{}", idStr, e.getMessage());
            }
        }

        log.info("批量删除文章成功：成功={}, 失败={}", successCount, failedIds.size());

        // 构建返回消息
        String message;
        if(failedIds.isEmpty()) {
            message = successCount == 1 ? "删除文章成功" : 
                String.format("成功删除 %d 篇文章", successCount);
        } else {
            message = String.format("成功删除 %d 篇文章，失败 %s", 
                successCount, String.join(", ", failedIds)
            );
        }

        return Result.success(message);
    }

    public void deleteArticle(Long id) {
        // 检查文章是否存在
        Article article = this.getById(id);
        if(article == null || article.getDeleted() == 1) {
            throw new BusinessException("文章不存在");
        }

        // 逻辑删除
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, id)
        .set(Article::getDeleted, 1)
        .set(Article::getUpdateTime, LocalDateTime.now());

        this.update(updateWrapper);

        log.info("删除文章成功：id={}, title={}", id, article.getTitle());
    }
}
