package com.syne.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.entity.Tags;
import com.syne.server.entity.dto.TagDTO;
import com.syne.server.entity.vo.TagListVO;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService extends IService<Tags> {

    /**
     * 分页查询标签列表
     *
     * @param pageQuery 分页查询参数
     * @param keyword   搜索关键词
     * @param sortBy    排序字段
     * @param sortOrder 排序方向
     * @return 标签分页列表
     */
    PageResult<TagListVO> getTagList(PageQuery pageQuery, String keyword, String sortBy, String sortOrder);

    /**
     * 根据ID获取标签详情
     *
     * @param id 标签ID
     * @return 标签详情
     */
    Tags getTagById(Long id);

    /**
     * 创建标签
     *
     * @param tagDTO 标签数据
     * @return 创建的标签
     */
    Tags createTag(TagDTO tagDTO);

    /**
     * 更新标签
     *
     * @param tagDTO 标签数据
     * @param id     标签ID
     * @return 更新后的标签
     */
    Tags updateTag(TagDTO tagDTO, Long id);

    /**
     * 删除标签
     *
     * @param ids 标签ID字符串
     * @return 删除结果
     */
    Result<String> deleteTags(String ids);

    /**
     * 根据文章ID获取标签列表
     *
     * @param articleId 文章ID
     * @return 标签列表
     */
    List<Tags> getTagsByArticleId(Long articleId);
}