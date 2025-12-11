package com.syne.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论实体类
 */
@Data
@TableName("comments")
@Schema(description = "评论实体类")
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "评论ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "文章ID")
    @TableField("article_id")
    private Long articleId;

    @Schema(description = "用户ID（游客为NULL）")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "父评论ID，用于多级回复")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "评论内容")
    @TableField("content")
    private String content;

    @Schema(description = "IP地址")
    @TableField("ip_address")
    private String ipAddress;

    @Schema(description = "评论状态: 1-正常, 2-待审核, 3-已删除")
    @TableField("status")
    private Integer status;
}