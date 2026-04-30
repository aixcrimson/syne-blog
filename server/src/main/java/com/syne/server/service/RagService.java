package com.syne.server.service;

import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * RAG 检索增强生成服务接口
 */
public interface RagService {

    /**
     * RAG 问答（同步）
     *
     * @param question  用户问题
     * @param history   对话历史
     * @param articleId 当前浏览的文章 ID（可选）
     * @return AI 回答
     */
    String chat(String question, List<Map<String, String>> history, Long articleId);

    /**
     * RAG 问答（流式）
     *
     * @param question  用户问题
     * @param history   对话历史
     * @param articleId 当前浏览的文章 ID（可选）
     * @return 流式回答
     */
    Flux<String> chatStream(String question, List<Map<String, String>> history, Long articleId);
}
