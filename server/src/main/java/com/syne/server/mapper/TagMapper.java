package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.model.entity.Tags;
import com.syne.server.model.vo.TagListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 标签Mapper接口
 */
@Mapper
public interface TagMapper extends BaseMapper<Tags> {

    /**
     * 查询标签列表
     *
     * @param keyword    搜索关键词
     * @param sortBy     排序字段
     * @param sortOrder  排序方向
     * @param offset     分页偏移量
     * @param pageSize   分页大小
     * @return 标签列表
     */
    List<TagListVO> selectTagList(
            @Param("keyword") String keyword,
            @Param("sortBy") String sortBy,
            @Param("sortOrder") String sortOrder,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );

    /**
     * 查询标签数量
     *
     * @param keyword 搜索关键词
     * @return 标签数量
     */
    Long countTags(@Param("keyword") String keyword);

    /**
     * 检查标签是否被文章使用
     *
     * @param tagId 标签ID
     * @return 使用次数
     */
    Integer countArticleTagsByTagId(@Param("tagId") Long tagId);

    /**
     * 查询所有标签列表
     *
     * @return 标签列表
     */
    List<TagListVO> selectAllTagList();

    /**
     * 查询已逻辑删除的同slug标签（绕过@TableLogic自动过滤）
     */
    @Select("SELECT * FROM tags WHERE slug = #{slug} AND deleted = 1 LIMIT 1")
    Tags selectDeletedBySlug(@Param("slug") String slug);

    /**
     * 查询已逻辑删除的同name标签（绕过@TableLogic自动过滤）
     */
    @Select("SELECT * FROM tags WHERE name = #{name} AND deleted = 1 LIMIT 1")
    Tags selectDeletedByName(@Param("name") String name);

    /**
     * 恢复已逻辑删除的标签（绕过@TableLogic自动过滤）
     */
    @Update("UPDATE tags SET name = #{name}, slug = #{slug}, color = #{color}, usage_count = #{usageCount}, deleted = 0, update_time = NOW() WHERE id = #{id}")
    int restoreDeletedTag(@Param("id") Long id, @Param("name") String name, @Param("slug") String slug, @Param("color") String color, @Param("usageCount") Integer usageCount);
}