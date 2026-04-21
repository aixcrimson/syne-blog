package com.syne.server.model.vo;

import com.syne.server.model.entity.Tags;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户端文章搜索索引视图对象
 */
@Data
@Schema(description = "用户端文章搜索索引视图对象")
public class ArticleSearchVO implements Serializable {

    public static final long serialVersionUID = 1L;

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "文章内容")
    private String content;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "标签列表")
    private List<Tags> tags;

    @Schema(description = "发布时间")
    private LocalDateTime publishedTime;

    @Schema(description = "浏览量")
    private Integer views;
}
