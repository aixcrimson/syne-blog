package com.syne.server.service;

/**
 * 文章向量化服务接口
 */
public interface EmbeddingService {

    /**
     * 同步单篇文章的向量
     *
     * @param articleId 文章ID
     */
    void syncArticle(Long articleId);

    /**
     * 同步所有文章的向量
     *
     * @return 同步的文章数量
     */
    int syncAllArticles();

    /**
     * 删除文章的向量
     *
     * @param articleId 文章ID
     */
    void deleteByArticleId(Long articleId);
}
