package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论列表视图对象
 */
@Data
@Schema(description = "评论列表视图对象")
public class CommentListVO implements Serializable {

    public static final long serialVersionUID = 1L;

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "文章ID")
    private Long articleId;

    @Schema(description = "文章标题")
    private String articleTitle;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "父评论ID")
    private Long parentId;

    @Schema(description = "父评论内容")
    private String parentContent;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "评论状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}