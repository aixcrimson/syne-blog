package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.entity.ArticleFavorite;
import com.syne.server.entity.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章收藏 Mapper 接口
 */
@Mapper
public interface ArticleFavoriteMapper extends BaseMapper<ArticleFavorite> {

    /**
     * 根据文章ID和用户ID查询点赞记录（包含已删除的）
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 收藏记录
     */
    ArticleFavorite selectByArticleAndUser(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 恢复已删除的点赞记录
     * @param id 点赞记录ID
     * @return 影响行数
     */
    int restoreFavorite(@Param("id") Long id);

    /**
     * 软删除点赞记录
     * @param id 点赞记录ID
     * @return 影响行数
     */
    int softDeleteFavorite(@Param("id") Long id);
}
