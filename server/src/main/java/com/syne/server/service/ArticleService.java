package com.syne.server.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.entity.Article;
import com.syne.server.entity.dto.ArticleDTO;
import com.syne.server.entity.vo.ArticleListVO;

/**
 * 文章服务接口
 */
public interface ArticleService extends IService<Article> {
    /**
     * 分页查询文章列表
     * @param pageQuery 分页参数
     * @param status 文章状态（可选，null 表示查询所有状态）
     * @return 分页结果
     */
    PageResult<ArticleListVO> getArticleList(PageQuery pageQuery, Integer status);

    /**
     * 管理端分页查询文章列表
     * @param pageQuery 分页参数
     * @param status 文章状态（可选，null 表示查询所有状态）
     * @param keyword 搜索关键字
     * @return 分页结果
     */
    PageResult<ArticleListVO> getAdminArticleList(PageQuery pageQuery, Integer status, String keyword);

    /**
     * 管理员根据ID获取管理员文章
     * @param id 文章ID
     * @return 文章实体
     */
    Article getAdminArticleById(Long id);

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
}