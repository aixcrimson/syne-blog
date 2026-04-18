package com.syne.server.service;

import reactor.core.publisher.Flux;

/**
 * AI 写作服务接口
 */
public interface AiWritingService {

    /**
     * AI 写作（同步）
     *
     * @param action  操作类型: outline, continue, polish, summary, title
     * @param content 输入内容
     * @return AI 生成的内容
     */
    String generate(String action, String content);

    /**
     * AI 写作（流式）
     *
     * @param action  操作类型
     * @param content 输入内容
     * @return 流式内容
     */
    Flux<String> generateStream(String action, String content);
}
