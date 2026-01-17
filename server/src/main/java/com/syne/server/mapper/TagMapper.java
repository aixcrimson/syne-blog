package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.model.entity.Tags;
import com.syne.server.model.vo.TagListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}