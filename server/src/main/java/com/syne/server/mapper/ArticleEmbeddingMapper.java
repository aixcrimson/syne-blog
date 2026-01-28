package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.model.entity.ArticleEmbedding;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章向量嵌入 Mapper
 */
@Mapper
public interface ArticleEmbeddingMapper extends BaseMapper<ArticleEmbedding> {

    /**
     * 根据文章ID删除所有向量
     */
    int deleteByArticleId(@Param("articleId") Long articleId);

    /**
     * 根据文章ID查询所有向量
     */
    List<ArticleEmbedding> selectByArticleId(@Param("articleId") Long articleId);
}
