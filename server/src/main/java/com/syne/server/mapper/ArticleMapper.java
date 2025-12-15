package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.entity.Article;
import com.syne.server.entity.vo.ArticleListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章 Mapper 接口
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 查询文章列表（包含作者、分类、标签信息）
     * @param offset   偏移量
     * @param pageSize 每页大小
     * @param status   文章状态（可选）
     * @return 文章列表
     */
    List<ArticleListVO> selectArticleList(
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize,
            @Param("status") Integer status
    );

    /**
     * 查询文章总数
     * @param status 文章状态（可选）
     * @return 总数
     */
    Long countArticles(@Param("status") Integer status);

    /**
     * 管理员查询文章列表
     * @param offset   偏移量
     * @param pageSize 每页大小
     * @param status   文章状态（可选）
     * @param keyword  搜索关键词（可选）
     * @return 文章列表
     */
    List<ArticleListVO> selectAdminArticleList(
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize,
            @Param("status") Integer status,
            @Param("keyword") String keyword
    );

    /**
     * 查询文章总数
     * @param status 文章状态（可选）
     * @param keyword  搜索关键词（可选）
     * @return 总数
     */
    Long countAdminArticles(@Param("status") Integer status, @Param("keyword") String keyword);
}
