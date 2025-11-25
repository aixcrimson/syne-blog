package com.syne.server.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.entity.Article;
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
}