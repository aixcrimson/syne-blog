package com.syne.server.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文章数据传输对象
 */
@Data
@Schema(description = "文章数据传输对象")
public class ArticleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "文章ID（更新接口需要）")
    private Long id;

    @Schema(description = "分类ID", required = true, example = "1")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "文章标题", required = true, example = "Vue 3 Composition API 深度解析")
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题长度不能超过200个字符")
    private String title;

    @Schema(description = "文章摘要", required = true, example = "本文详细介绍了 Vue 3 Composition API 的核心概念和使用方法")
    private String summary;

    @Schema(description = "文章内容", required = true)
    @NotBlank(message = "文章内容不能为空")
    private String content;

    @Schema(description = "封面图片URL", example = "/covers/vue3.jpg")
    private String coverImage;

    @Schema(description = "标签ID列表", example = "[1, 2, 3]")
    private List<Long> tagIds;

    @Schema(description = "文章状态：1-已发布，2-草稿，3-已下架", required = true, example = "2")
    @NotNull(message = "文章状态不能为空")
    private Integer status;

    @Schema(description = "是否置顶：0-否，1-是", example = "0")
    private Integer isTop;

    @Schema(description = "是否推荐：0-否，1-是", example = "0")
    private Integer isRecommend;
}