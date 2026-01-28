package com.syne.server.service.impl;

import com.syne.server.exception.BusinessException;
import com.syne.server.service.AiWritingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AI 写作服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.enabled", havingValue = "true", matchIfMissing = true)
public class AiWritingServiceImpl implements AiWritingService {

    private final ChatClient.Builder chatClientBuilder;

    /**
     * 写作操作对应的系统提示词
     */
    private static final Map<String, String> WRITING_PROMPTS = Map.of(
            "outline", "你是一位专业的写作助手。请根据用户提供的主题，生成一个结构清晰、逻辑严谨的文章大纲。大纲应包含标题和多级子标题，使用 Markdown 格式。",
            "continue", "你是一位专业的写作助手。请根据用户提供的上下文，自然流畅地续写内容。保持与原文风格一致，内容连贯。",
            "polish", "你是一位专业的文字编辑。请润色用户提供的文本，改进表达、修正语法、增强可读性，但保持原意不变。",
            "summary", "你是一位专业的内容编辑。请为用户提供的文章生成一段简洁的摘要，控制在 150 字以内，突出核心要点。",
            "title", "你是一位专业的标题创作者。请根据用户提供的文章内容，生成 5 个吸引人的标题建议，每行一个。"
    );

    @Override
    public String generate(String action, String content) {
        String systemPrompt = getSystemPrompt(action);

        try {
            ChatClient chatClient = chatClientBuilder.build();
            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(content)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("AI 写作失败: {}", e.getMessage(), e);
            throw new BusinessException("AI 服务暂时不可用，请稍后重试");
        }
    }

    @Override
    public Flux<String> generateStream(String action, String content) {
        String systemPrompt = getSystemPrompt(action);

        try {
            ChatClient chatClient = chatClientBuilder.build();
            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(content)
                    .stream()
                    .content();
        } catch (Exception e) {
            log.error("AI 写作流式请求失败: {}", e.getMessage(), e);
            return Flux.error(new BusinessException("AI 服务暂时不可用，请稍后重试"));
        }
    }

    private String getSystemPrompt(String action) {
        String systemPrompt = WRITING_PROMPTS.get(action);
        if (systemPrompt == null) {
            throw new BusinessException("不支持的操作类型: " + action);
        }
        return systemPrompt;
    }
}
