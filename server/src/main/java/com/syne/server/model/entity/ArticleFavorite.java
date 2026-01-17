package com.syne.server.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章收藏实体类
 */
@Data
@TableName("article_favorites")
@Schema(description = "文章收藏实体类")
@EqualsAndHashCode(callSuper = true)
public class ArticleFavorite extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "收藏ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "文章ID")
    @TableField("article_id")
    private Long articleId;
}