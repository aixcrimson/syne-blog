package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章标签关联 Mapper 接口
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    /**
     * 批量插入文章标签关联
     *
     * @param articleId 文章ID
     * @param tagIds    标签ID列表
     * @return 插入记录数
     */
    int insertByArticleAndTags(@Param("articleId") Long articleId,
                               @Param("tagIds") List<Long> tagIds);

    /**
     * 根据文章ID删除所有标签关联
     *
     * @param articleId 文章ID
     * @return 删除记录数
     */
    int deleteByArticleId(@Param("articleId") Long articleId);

    /**
     * 根据文章ID和标签ID列表删除特定关联
     * （用于更新时精确删除）
     *
     * @param articleId 文章ID
     * @param tagIds    标签ID列表
     * @return 删除记录数
     */
    int deleteByArticleIdAndTags(@Param("articleId") Long articleId,
                                 @Param("tagIds") List<Long> tagIds);

    /**
     * 根据文章ID查询关联的标签ID列表
     *
     * @param articleId 文章ID
     * @return 标签ID列表
     */
    List<Long> selectTagIdsByArticleId(@Param("articleId") Long articleId);
}