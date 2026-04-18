package com.syne.server.config.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 功能配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    /**
     * 是否启用 AI 功能
     */
    private boolean enabled = true;

    /**
     * RAG 检索配置
     */
    private RagConfig rag = new RagConfig();

    @Data
    public static class RagConfig {
        /**
         * 是否启用 RAG 检索
         */
        private boolean enabled = true;

        /**
         * 检索相关文档数量
         */
        private int topK = 5;

        /**
         * 相似度阈值（0-1）
         */
        private double similarityThreshold = 0.7;
    }
}
