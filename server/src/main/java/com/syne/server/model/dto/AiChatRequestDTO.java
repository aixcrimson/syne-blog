package com.syne.server.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * AI 聊天请求 DTO
 */
@Data
public class AiChatRequestDTO {

    /**
     * 用户消息
     */
    @NotBlank(message = "消息不能为空")
    private String message;

    /**
     * 对话历史（可选）
     */
    private List<ChatMessage> history;

    /**
     * 聊天消息
     */
    @Data
    public static class ChatMessage {
        /**
         * 角色: user / assistant
         */
        private String role;

        /**
         * 内容
         */
        private String content;
    }
}
