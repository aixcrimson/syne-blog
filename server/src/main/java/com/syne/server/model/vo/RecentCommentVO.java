package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 最近评论VO（仪表盘用）
 */
@Data
@Schema(description = "最近评论")
public class RecentCommentVO {

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "文章标题")
    private String articleTitle;

    @Schema(description = "文章ID")
    private Long articleId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "评论状态")
    private Integer status;

    @Schema(description = "IP地址")
    private String ipAddress;
}