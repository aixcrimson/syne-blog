package com.syne.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章点赞实体类
 */
@Data
@TableName("article_likes")
@Schema(description = "文章点赞实体类")
@EqualsAndHashCode(callSuper = true)
public class ArticleLike extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "点赞ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "文章ID")
    @TableField("article_id")
    private Long articleId;

    @Schema(description = "IP地址")
    @TableField("ip_address")
    private String ipAddress;
}