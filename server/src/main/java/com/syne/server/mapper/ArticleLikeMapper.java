package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.entity.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章点赞 Mapper 接口
 */
@Mapper
public interface ArticleLikeMapper extends BaseMapper<ArticleLike> {

    /**
     * 根据文章ID和用户ID查询点赞记录（包含已删除的）
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 点赞记录
     */
    ArticleLike selectByArticleAndUser(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 根据文章ID和IP地址查询点赞记录（包含已删除的）
     * @param articleId 文章ID
     * @param ipAddress IP地址
     * @return 点赞记录
     */
    ArticleLike selectByArticleAndIp(@Param("articleId") Long articleId, @Param("ipAddress") String ipAddress);

    /**
     * 恢复已删除的点赞记录
     * @param id 点赞记录ID
     * @return 影响行数
     */
    int restoreLike(@Param("id") Long id);

    /**
     * 软删除点赞记录
     * @param id 点赞记录ID
     * @return 影响行数
     */
    int softDeleteLike(@Param("id") Long id);
}
