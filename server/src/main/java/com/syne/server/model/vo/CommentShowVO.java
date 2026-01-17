package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户端评论展示视图对象（树形结构）
 */
@Data
@Schema(description = "用户端评论展示视图对象")
public class CommentShowVO implements Serializable {

    public static final long serialVersionUID = 1L;

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "文章ID")
    private Long articleId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "父评论ID")
    private Long parentId;

    @Schema(description = "被回复用户的用户名（回复时显示）")
    private String replyToUsername;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "子评论列表（回复）")
    private List<CommentShowVO> children;
}
