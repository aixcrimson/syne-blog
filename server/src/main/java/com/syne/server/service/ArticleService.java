package com.syne.server.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.model.entity.Article;
import com.syne.server.model.dto.ArticleDTO;
import com.syne.server.model.vo.ArticleDetailVO;
import com.syne.server.model.vo.ArticleListVO;
import com.syne.server.model.vo.ArticleSearchVO;


import java.util.List;
import java.util.Map;

/**
 * 文章服务接口
 */
public interface ArticleService extends IService<Article> {


    /**
     * 管理端分页查询文章列表
     * @param pageQuery 分页参数
     * @param status 文章状态（可选，null 表示查询所有状态）
     * @param categoryId 文章分类ID（可选，null 表示查询所有分类）
     * @param keyword 搜索关键字
     * @return 分页结果
     */
    PageResult<ArticleListVO> getAdminArticleList(PageQuery pageQuery, Integer status, Long categoryId, String keyword);

    /**
     * 管理员根据ID获取管理员文章详情
     * @param id 文章ID
     * @return 文章详情
     */
    ArticleDetailVO getAdminArticleById(Long id);

    /**
     * 创建文章
     * @param articleDTO 文章数据
     * @return 创建的文章
     */
    Article createArticle(ArticleDTO articleDTO);

    /**
     * 更新文章
     * @param articleDTO 文章数据
     * @return 更新后的文章
     */
    Article updateArticle(ArticleDTO articleDTO);

    /**
     * 删除文章
     * @param ids 文章ID
     * @return 删除结果
     */
    Result<String> deleteArticles(String ids);

    /**
     * 切换文章置顶状态
     * @param id 文章ID
     * @return 切换结果
     */
    Result<String> toggleArticleTop(Long id);

    /**
     * 切换文章推荐状态
     * @param id 文章ID
     * @return 切换结果
     */
    Result<String> toggleArticleRecommend(Long id);

    /**
     * 更新文章状态
     * @param id 文章ID
     * @param status 文章状态
     * @return 更新结果
     */
    Result<String> updateStatus(Long id, Integer status);

    /**
     * 用户端分页查询文章列表
     * @param pageQuery 分页参数
     * @param keyword 搜索关键字
     * @param categoryId 文章分类ID
     * @param tagIds 文章标签ID列表
     * @return 分页结果
     */
    PageResult<ArticleListVO> getUserArticleList(PageQuery pageQuery, String keyword, Long categoryId, List<Long> tagIds);

    /**
     * 获取用户端搜索索引
     * @return 搜索索引列表
     */
    List<ArticleSearchVO> getSearchIndexArticles();

    /**
     * 用户端获取文章详情
     * @param id 文章ID
     * @return 文章详情
     */
    ArticleDetailVO getUserArticleById(Long id);


    /**
     * 获取推荐文章列表
     * @param limit 限制数量
     * @return 文章列表
     */
    List<ArticleListVO> getRecommendedArticleList(Integer limit);

    /**
     * 切换文章喜欢或不喜欢
     * @param id 文章ID
     * @return 切换结果
     */
    Result<Map<String, Object>> toggleArticleLike(Long id);

    /**
     * 切换文章收藏或不收藏
     * @param id 文章ID
     * @return 切换结果
     */
    Result<Map<String, Object>> toggleArticleFavorite(Long id);

    /**
     * 给文章增加浏览量
     * @param id 文章ID
     * @return 结果
     */
    Result<Map<String, Object>> increaseViews(Long id);

    /**
     * 获取用户点赞的文章列表
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<ArticleListVO> getLikedArticleList(PageQuery pageQuery);

    /**
     * 获取用户收藏的文章列表
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<ArticleListVO> getFavoriteArticleList(PageQuery pageQuery);
}