package com.syne.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章标签关联实体类
 */
@Data
@TableName("article_tags")
@Schema(description = "文章标签关联实体类")
@EqualsAndHashCode(callSuper = true)
public class ArticleTag extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "关联ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "文章ID")
    @TableField("article_id")
    private Long articleId;

    @Schema(description = "标签ID")
    @TableField("tag_id")
    private Long tagId;
}