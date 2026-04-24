package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.model.entity.*;
import com.syne.server.model.dto.ArticleDTO;
import com.syne.server.model.vo.ArticleDetailVO;
import com.syne.server.model.vo.ArticleListVO;
import com.syne.server.model.vo.ArticleSearchVO;

import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.*;
import com.syne.server.service.ArticleService;
import com.syne.server.service.TagService;
import com.syne.server.utils.IpUtils;
import com.syne.server.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private static final Duration VIEW_WINDOW = Duration.ofMinutes(5);

    private final ArticleMapper articleMapper;
    private final ArticleLikeMapper articleLikeMapper;
    private final ArticleFavoriteMapper articleFavoriteMapper;
    private final ArticleTagMapper articleTagMapper;
    private final TagMapper tagMapper;
    private final CategoryMapper categoryMapper;
    private final TagService tagService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public PageResult<ArticleListVO> getAdminArticleList(PageQuery pageQuery, Integer status, Long categoryId, String keyword) {
        // 查询文章列表
        List<ArticleListVO> list = articleMapper.selectAdminArticleList(
                pageQuery.getOffset(),
                pageQuery.getPageSize(),
                status,
                categoryId,
                keyword
        );

        // 处理标签字段
        list.forEach(article -> {
            article.setTags(tagService.getTagsByArticleId(article.getId()));
        });

        // 查询总数
        Long total = articleMapper.countAdminArticles(status, categoryId, keyword);

        // 构建分页结果
        return PageResult.build(
                pageQuery.getPage(),
                pageQuery.getPageSize(),
                total,
                list
        );
    }

    @Override
    public ArticleDetailVO getAdminArticleById(Long id) {
        // 获取文章基本信息
        Article article = this.getById(id);
        if(article == null || article.getDeleted() == 1) {
            throw new BusinessException("文章不存在");
        }

        // 获取文章标签列表
        List<Tags> tags = tagService.getTagsByArticleId(id);

        // 构建文章详情VO
        ArticleDetailVO articleDetailVO = new ArticleDetailVO();
        articleDetailVO.setId(article.getId());
        articleDetailVO.setCategoryId(article.getCategoryId());
        articleDetailVO.setTitle(article.getTitle());
        articleDetailVO.setSummary(article.getSummary());
        articleDetailVO.setContent(article.getContent());
        articleDetailVO.setCoverImage(article.getCoverImage());
        articleDetailVO.setViews(article.getViews());
        articleDetailVO.setLikes(article.getLikes());
        articleDetailVO.setFavorites(article.getFavorites());
        articleDetailVO.setCommentsCount(article.getCommentsCount());
        articleDetailVO.setStatus(article.getStatus());
        articleDetailVO.setIsTop(article.getIsTop());
        articleDetailVO.setIsRecommend(article.getIsRecommend());
        articleDetailVO.setPublishedTime(article.getPublishedTime());
        articleDetailVO.setCreateTime(article.getCreateTime());
        articleDetailVO.setUpdateTime(article.getUpdateTime());
        articleDetailVO.setTags(tags);

        return articleDetailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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

        // 3.保存文章
        this.save(article);

        // 4.处理标签关联
        if(articleDTO.getTagIds() != null && !articleDTO.getTagIds().isEmpty()) {
            processArticleTags(article.getId(), null, articleDTO.getTagIds());
        }

        log.info("创建文章成功：id={}, title={}", article.getId(), article.getTitle());
        return article;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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

        // 3.处理标签关联（传入空列表作为 oldTagIds 标记，触发删除旧关联的逻辑）
        List<Long> newTagIds = articleDTO.getTagIds() != null ? articleDTO.getTagIds() : new ArrayList<>();
        processArticleTags(article.getId(), new ArrayList<>(), newTagIds);

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

    @Override
    public Result<String> toggleArticleTop(Long id) {
        // 检查文章是否存在
        Article article = this.getById(id);
        if(article == null || article.getDeleted() == 1) {
            throw new BusinessException("文章不存在");
        }

        // 切换置顶状态
        int newTopStatus = article.getIsTop() == 0 ? 1 : 0;
        article.setIsTop(newTopStatus);
        this.updateById(article);

        String message = newTopStatus == 1 ? "文章已置顶" : "文章已取消置顶";
        log.info("切换文章置顶状态成功：id={}, title={}, newTopStatus={}", id, article.getTitle(), newTopStatus);

        return Result.success(message);
    }

    @Override
    public Result<String> toggleArticleRecommend(Long id) {
        // 检查文章是否存在
        Article article = this.getById(id);
        if(article == null || article.getDeleted() == 1) {
            throw new BusinessException("文章不存在");
        }

        // 切换推荐状态
        int newRecommendStatus = article.getIsRecommend() == 0 ? 1 : 0;
        article.setIsRecommend(newRecommendStatus);
        this.updateById(article);

        String message = newRecommendStatus == 1 ? "文章已推荐" : "文章已取消推荐";
        log.info("切换文章推荐状态成功：id={}, title={}, newRecommendStatus={}", id, article.getTitle(), newRecommendStatus);

        return Result.success(message);
    }

    @Override
    public Result<String> updateStatus(Long id, Integer status) {
        // 检查文章是否存在
        Article article = this.getById(id);
        if(article == null || article.getDeleted() == 1) {
            throw new BusinessException("文章不存在");
        }

        // 如果状态从未发布变为已发布，更新发布时间
        if (status == 1 && (article.getStatus() == null || article.getStatus() != 1)) {
            article.setPublishedTime(LocalDateTime.now());
        }

        // 更新状态
        article.setStatus(status);
        this.updateById(article);

        log.info("更新文章状态成功：id={}, title={}, newStatus={}", id, article.getTitle(), status);

        return Result.success("文章状态更新成功");
    }

    /**
     * 处理文章标签关联
     *
     * @param articleId   文章ID
     * @param oldTagIds   旧标签ID列表（更新时使用，创建时传null）
     * @param newTagIds   新标签ID列表
     */
    private void processArticleTags(Long articleId, List<Long> oldTagIds, List<Long> newTagIds) {
        // 1.验证标签ID列表
        if(newTagIds != null && !newTagIds.isEmpty()) {
            // 去重并检查标签是否存在
            newTagIds = newTagIds.stream()
                    .distinct()
                    .filter(tagId -> tagId != null && tagId > 0)
                    .collect(Collectors.toList());

            if(!newTagIds.isEmpty()) {
                // 批量验证标签是否存在
                for(Long tagId : newTagIds) {
                    if(tagMapper.selectById(tagId) == null) {
                        throw new BusinessException("标签不存在：tagId=" + tagId);
                    }
                }
            }
        }

        // 2.如果是更新操作，先删除旧的关联
        if(oldTagIds != null) {
            // 删除所有旧的关联
            articleTagMapper.deleteByArticleId(articleId);
        }

        // 3.插入新的关联（如果存在）
        if(newTagIds != null && !newTagIds.isEmpty()) {
            // 检查是否有关联变更（更新时）
            if(oldTagIds != null) {
                List<Long> oldTagIdsSorted = oldTagIds.stream()
                        .filter(id -> id != null)
                        .sorted()
                        .collect(Collectors.toList());
                List<Long> newTagIdsSorted = newTagIds.stream()
                        .filter(id -> id != null)
                        .sorted()
                        .collect(Collectors.toList());

                // 如果没有变化，直接返回
                if(oldTagIdsSorted.equals(newTagIdsSorted)) {
                    return;
                }
            }

            // 批量插入新的关联
            int insertCount = articleTagMapper.insertByArticleAndTags(articleId, newTagIds);
            if(insertCount != newTagIds.size()) {
                throw new BusinessException("标签关联插入失败");
            }
        } else if(oldTagIds == null) {
            // 创建时如果没有标签，不做任何操作
        } else {
            // 更新时如果新标签为空，确保删除所有旧关联
            articleTagMapper.deleteByArticleId(articleId);
        }
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
        articleTagMapper.deleteByArticleId(id);

        log.info("删除文章成功：id={}, title={}", id, article.getTitle());
    }


    /* ========================================用户端相关接口======================================== */


    @Override
    public PageResult<ArticleListVO> getUserArticleList(PageQuery pageQuery, String keyword, Long categoryId, List<Long> tagIds){
        // 查询文章列表
        List<ArticleListVO> list = articleMapper.selectUserArticleList(
                pageQuery.getOffset(),
                pageQuery.getPageSize(),
                keyword,
                categoryId,
                tagIds
        );

        // 处理标签字段
        list.forEach(article -> {
            article.setTags(tagService.getTagsByArticleId(article.getId()));
        });

        // 查询总数
        Long total = articleMapper.countUserArticles(keyword, categoryId, tagIds);

        // 构建分页结果
        return PageResult.build(
                pageQuery.getPage(),
                pageQuery.getPageSize(),
                total,
                list
        );
    }

    @Override
    public List<ArticleSearchVO> getSearchIndexArticles() {
        List<Article> articles = this.lambdaQuery()
                .eq(Article::getDeleted, 0)
                .eq(Article::getStatus, 1)
                .orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getPublishedTime)
                .orderByDesc(Article::getCreateTime)
                .list();

        return articles.stream().map(article -> {
            Category category = article.getCategoryId() != null
                    ? categoryMapper.selectById(article.getCategoryId())
                    : null;

            ArticleSearchVO articleSearchVO = new ArticleSearchVO();
            articleSearchVO.setId(article.getId());
            articleSearchVO.setTitle(article.getTitle());
            articleSearchVO.setSummary(article.getSummary());
            articleSearchVO.setContent(article.getContent());
            articleSearchVO.setCategoryName(category != null ? category.getName() : null);
            articleSearchVO.setTags(tagService.getTagsByArticleId(article.getId()));
            articleSearchVO.setPublishedTime(article.getPublishedTime());
            articleSearchVO.setViews(article.getViews());
            return articleSearchVO;
        }).collect(Collectors.toList());
    }

    @Override
    public ArticleDetailVO getUserArticleById(Long id) {

        // 获取文章基本信息
        Article article = this.getById(id);
        if(article == null || article.getDeleted() == 1) {
            throw new BusinessException("文章不存在");
        }

        // 获取文章分类名称
        Category category = categoryMapper.selectById(article.getCategoryId());

        // 获取文章标签列表
        List<Tags> tags = tagService.getTagsByArticleId(id);

        // 构建文章详情VO
        ArticleDetailVO articleDetailVO = new ArticleDetailVO();
        articleDetailVO.setId(article.getId());
        articleDetailVO.setCategoryId(article.getCategoryId());
        articleDetailVO.setCategoryName(category != null ? category.getName() : null);
        articleDetailVO.setTitle(article.getTitle());
        articleDetailVO.setSummary(article.getSummary());
        articleDetailVO.setContent(article.getContent());
        articleDetailVO.setCoverImage(article.getCoverImage());
        articleDetailVO.setViews(article.getViews());
        articleDetailVO.setLikes(article.getLikes());
        articleDetailVO.setFavorites(article.getFavorites());
        articleDetailVO.setCommentsCount(article.getCommentsCount());
        articleDetailVO.setStatus(article.getStatus());
        articleDetailVO.setIsTop(article.getIsTop());
        articleDetailVO.setIsRecommend(article.getIsRecommend());
        articleDetailVO.setPublishedTime(article.getPublishedTime());
        articleDetailVO.setCreateTime(article.getCreateTime());
        articleDetailVO.setUpdateTime(article.getUpdateTime());
        articleDetailVO.setTags(tags);

        // 判断是否点赞
        Long userId = SecurityUtils.getCurrentUserId();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddress = IpUtils.getClientIp(request);

        ArticleLike existingLike;
        if (userId != null) {
            existingLike = articleLikeMapper.selectByArticleAndUser(id, userId);
        } else {
            existingLike = articleLikeMapper.selectByArticleAndIp(id, ipAddress);
        }
        articleDetailVO.setIsLiked(existingLike != null && existingLike.getDeleted() == 0);

        // 判断是否收藏
        if (userId != null) {
            ArticleFavorite existingFavorite = articleFavoriteMapper.selectByArticleAndUser(id, userId);
            articleDetailVO.setIsFavorited(existingFavorite != null && existingFavorite.getDeleted() == 0);
        } else {
            articleDetailVO.setIsFavorited(false);
        }

        return articleDetailVO;
    }

    @Override
    public List<ArticleListVO> getRecommendedArticleList(Integer limit){
        // 查询推荐文章列表（不传筛选条件）
        List<ArticleListVO> list = articleMapper.selectUserArticleList(
                0,
                limit,
                null,
                null,
                null
        );

        // 处理标签字段
        list.forEach(article -> {
            article.setTags(tagService.getTagsByArticleId(article.getId()));
        });

        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> toggleArticleLike(Long id){
        // 检查文章是否存在
        Article article = this.getById(id);
        if(article == null || article.getDeleted() == 1) {
            throw new BusinessException("文章不存在");
        }

        // 获取当前用户ID（如果已登录）
        Long userId = SecurityUtils.getCurrentUserId();

        // 获取客户端IP地址
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddress = IpUtils.getClientIp(request);

        ArticleLike existingLike;
        boolean isLiked;

        if (userId != null) {
            // 登录用户：根据用户ID查询
            existingLike = articleLikeMapper.selectByArticleAndUser(id, userId);
        } else {
            // 游客：根据IP地址查询
            existingLike = articleLikeMapper.selectByArticleAndIp(id, ipAddress);
        }

        if (existingLike != null) {
            // 已有记录
            if (existingLike.getDeleted() == 0) {
                // 已点赞，取消点赞
                articleLikeMapper.softDeleteLike(existingLike.getId());
                // 文章点赞数 -1
                article.setLikes(article.getLikes() - 1);
                this.updateById(article);
                isLiked = false;
                log.info("取消点赞成功：articleId={}, userId={}, ip={}", id, userId, ipAddress);
            } else {
                // 已取消点赞，恢复点赞
                articleLikeMapper.restoreLike(existingLike.getId());
                // 文章点赞数 +1
                article.setLikes(article.getLikes() + 1);
                this.updateById(article);
                isLiked = true;
                log.info("恢复点赞成功：articleId={}, userId={}, ip={}", id, userId, ipAddress);
            }
        } else {
            // 没有记录，新增点赞
            ArticleLike newLike = new ArticleLike();
            newLike.setArticleId(id);
            newLike.setIpAddress(ipAddress);
            newLike.setCreateBy(userId); // 登录用户设置userId，游客为null
            newLike.setDeleted(0);
            articleLikeMapper.insert(newLike);
            // 文章点赞数 +1
            article.setLikes(article.getLikes() + 1);
            this.updateById(article);
            isLiked = true;
            log.info("新增点赞成功：articleId={}, userId={}, ip={}", id, userId, ipAddress);
        }

        Map<String, Object> data = new java.util.HashMap<>();
        data.put("liked", isLiked);
        data.put("likes", article.getLikes());

        return Result.success(data);
    }

    @Override
    public Result<Map<String, Object>> toggleArticleFavorite(Long id){
        // 检查文章是否存在
        Article article = this.getById(id);
        if(article == null || article.getDeleted() == 1) {
            throw new BusinessException("文章不存在");
        }

        // 获取当前用户ID（需要登录才能收藏）
        Long userId = SecurityUtils.getCurrentUserId();

        // 根据用户ID和文章ID是否已收藏
        ArticleFavorite existingFavorite = articleFavoriteMapper.selectByArticleAndUser(id, userId);
        boolean isFavorite;

        if(existingFavorite != null) {
            // 已有记录
            if(existingFavorite.getDeleted() == 0){
                // 已收藏，取消收藏
                articleFavoriteMapper.softDeleteFavorite(existingFavorite.getId());
                // 文章收藏数 - 1
                article.setFavorites(article.getFavorites() - 1);
                this.updateById(article);
                isFavorite = false;
                log.info("取消收藏成功：articleId={}, userId={}", id, userId);
            }else{
                // 已取消收藏，恢复收藏
                articleFavoriteMapper.restoreFavorite(existingFavorite.getId());
                // 文章收藏数 + 1
                article.setFavorites(article.getFavorites() + 1);
                this.updateById(article);
                isFavorite = true;
                log.info("恢复收藏成功：articleId={}, userId={}", id, userId);
            }
        } else {
            // 没有记录，新增收藏
            ArticleFavorite newFavorite = new ArticleFavorite();
            newFavorite.setArticleId(id);
            newFavorite.setCreateBy(userId);
            newFavorite.setDeleted(0);
            articleFavoriteMapper.insert(newFavorite);
            // 文章收藏数 + 1
            article.setFavorites(article.getFavorites() + 1);
            this.updateById(article);
            isFavorite = true;
            log.info("新增收藏成功：articleId={}, userId={}", id, userId);
        }

        Map<String, Object> data = new java.util.HashMap<>();
        data.put("favorited", isFavorite);
        data.put("favorites", article.getFavorites());

        return Result.success(data);
    }

    @Override
    public Result<Map<String, Object>> increaseViews(Long id){
        // 检查文章是否存在
        Article article = this.getById(id);
        if(article == null || article.getDeleted() == 1) {
            throw new BusinessException("文章不存在");
        }

        // 获取当前用户ID（如果已登录）
        Long userId = SecurityUtils.getCurrentUserId();

        // 获取客户端IP地址
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddress = IpUtils.getClientIp(request);

        String viewerKey = userId != null ? "u:" + userId : "ip:" + ipAddress;
        String cacheKey = "article:view:limit:" + id + ":" + viewerKey;
        Boolean firstView = stringRedisTemplate.opsForValue().setIfAbsent(cacheKey, "1", VIEW_WINDOW);

        if(Boolean.TRUE.equals(firstView)) {
            article.setViews(article.getViews() + 1);
            this.updateById(article);
        }

        Map<String, Object> data = new java.util.HashMap<>();
        data.put("views", article.getViews());

        return Result.success(data);
    }

    @Override
    public PageResult<ArticleListVO> getLikedArticleList(PageQuery pageQuery) {
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();

        // 查询点赞的文章列表
        List<ArticleListVO> list = articleMapper.selectLikedArticles(
                pageQuery.getOffset(),
                pageQuery.getPageSize(),
                userId
        );

        // 处理标签
        list.forEach(article -> article.setTags(tagService.getTagsByArticleId(article.getId())));

        // 查询总数
        Long total = articleMapper.countLikedArticles(userId);

        return PageResult.build(pageQuery.getPage(), pageQuery.getPageSize(), total, list);
    }

    @Override
    public PageResult<ArticleListVO> getFavoriteArticleList(PageQuery pageQuery){
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();

        // 查询收藏的文章列表
        List<ArticleListVO> list = articleMapper.selectFavoriteArticles(
            pageQuery.getOffset(),
            pageQuery.getPageSize(),
            userId
        );

        // 处理标签
        list.forEach(article -> article.setTags(tagService.getTagsByArticleId(article.getId())));

        // 查询总数
        Long total = articleMapper.countFavoriteArticles(userId);

        return PageResult.build(pageQuery.getPage(), pageQuery.getPageSize(), total, list);
    }

}
