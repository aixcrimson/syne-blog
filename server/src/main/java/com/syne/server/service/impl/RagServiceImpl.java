package com.syne.server.service.impl;

import com.syne.server.config.ai.AiProperties;
import com.syne.server.exception.BusinessException;
import com.syne.server.service.RagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RAG 检索增强生成服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.enabled", havingValue = "true", matchIfMissing = true)
public class RagServiceImpl implements RagService {

    private final ChatClient.Builder chatClientBuilder;
    private final VectorStore vectorStore;
    private final AiProperties aiProperties;

    /**
     * 系统提示词模板
     */
    private static final String SYSTEM_PROMPT_TEMPLATE = """
            你是 Syne Blog 的 AI 助手，一个友好、专业的博客问答机器人。

            你的职责：
            1. 基于提供的博客文章内容回答用户问题
            2. 如果问题与提供的内容无关，礼貌地说明
            3. 回答要简洁明了，使用中文

            以下是相关的博客内容作为参考：
            ---
            %s
            ---

            请基于以上内容回答用户的问题。如果内容中没有相关信息，请诚实地说明。
            """;

    @Override
    public String chat(String question, List<Map<String, String>> history) {
        // 1. 检索相关文档
        String context = retrieveContext(question);

        // 2. 构建系统提示词
        String systemPrompt = String.format(SYSTEM_PROMPT_TEMPLATE, context);

        try {
            ChatClient chatClient = chatClientBuilder.build();
            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(question)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("RAG 问答失败: {}", e.getMessage(), e);
            throw new BusinessException("AI 服务暂时不可用，请稍后重试");
        }
    }

    @Override
    public Flux<String> chatStream(String question, List<Map<String, String>> history) {
        // 1. 检索相关文档
        String context = retrieveContext(question);

        // 2. 构建系统提示词
        String systemPrompt = String.format(SYSTEM_PROMPT_TEMPLATE, context);

        try {
            ChatClient chatClient = chatClientBuilder.build();
            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(question)
                    .stream()
                    .content();
        } catch (Exception e) {
            log.error("RAG 流式问答失败: {}", e.getMessage(), e);
            return Flux.error(new BusinessException("AI 服务暂时不可用，请稍后重试"));
        }
    }

    /**
     * 从向量数据库检索相关上下文
     */
    private String retrieveContext(String question) {
        try {
            int topK = aiProperties.getRag().getTopK();
            double threshold = aiProperties.getRag().getSimilarityThreshold();

            SearchRequest searchRequest = SearchRequest.query(question)
                    .withTopK(topK)
                    .withSimilarityThreshold(threshold);

            List<Document> documents = vectorStore.similaritySearch(searchRequest);

            if (documents.isEmpty()) {
                return "暂无相关博客内容";
            }

            return documents.stream()
                    .map(Document::getContent)
                    .collect(Collectors.joining("\n\n"));
        } catch (Exception e) {
            log.warn("向量检索失败，使用空上下文: {}", e.getMessage());
            return "暂无相关博客内容";
        }
    }
}
