package com.syne.server.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建评论 DTO
 */
@Data
@Schema(description = "创建评论请求参数")
public class CommentCreateDTO {

    @Schema(description = "文章ID", required = true)
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    @Schema(description = "父评论ID（回复时使用）")
    private Long parentId;

    @Schema(description = "评论内容", required = true)
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度应在1-1000字符之间")
    private String content;
}
