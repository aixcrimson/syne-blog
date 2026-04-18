package com.syne.server.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI 写作请求 DTO
 */
@Data
public class AiWritingRequestDTO {

    /**
     * 操作类型: outline(大纲), continue(续写), polish(润色), summary(摘要), title(标题)
     */
    @NotBlank(message = "操作类型不能为空")
    private String action;

    /**
     * 输入内容（主题/选中文本/文章内容）
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 额外上下文（可选）
     */
    private String context;
}
