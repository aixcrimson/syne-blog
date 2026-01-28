package com.syne.server.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章向量嵌入实体
 */
@Data
@TableName("article_embeddings")
public class ArticleEmbedding {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的文章ID
     */
    private Long articleId;

    /**
     * 分块索引
     */
    private Integer chunkIndex;

    /**
     * 分块内容
     */
    private String chunkContent;

    /**
     * 向量嵌入（存储为字符串，实际是 vector 类型）
     */
    private String embedding;

    /**
     * 元数据 JSON
     */
    private String metadata;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
