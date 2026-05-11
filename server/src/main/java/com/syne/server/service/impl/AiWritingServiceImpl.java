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
import java.time.Duration;
import reactor.util.retry.Retry;

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
            "outline", "你是一位专业的写作助手。请根据用户提供的主题，生成文章大纲。严格要求：\n1. 使用标准 Markdown 格式，标题的 '#' 和列表的 '-' 后面【必须加一个空格】！\n2. 绝对禁止使用 ```markdown 代码块包裹内容！直接输出纯文本。\n3. 层级之间保留空行。\n【正确示例】:\n# 文章主标题\n\n## 一、核心部分\n- 第一点\n- 第二点",
            "continue", "你是一位专业的写作助手。请根据用户提供的上下文，自然流畅地续写内容。保持与原文风格一致，内容连贯。",
            "polish", "你是一位专业的文字编辑。请润色用户提供的文本，改进表达、修正语法、增强可读性，但保持原意不变。",
            "summary", "你是一位专业的内容编辑。请为用户提供的文章生成一段简洁的摘要，控制在 150 字以内，突出核心要点。",
            "title", "你是一位专业的标题创作者。请根据用户提供的文章内容，生成 5 个吸引人的标题建议，每个标题控制在 50 个字以内，每行一个，只输出标题文本，不要加编号或其他格式。"
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
            if (e.getMessage() != null && e.getMessage().contains("429")) {
                throw new BusinessException("AI 服务请求过于频繁，请稍后重试");
            }
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
                    .content()
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                            .filter(throwable -> throwable.getMessage() != null && throwable.getMessage().contains("429")))
                    .onErrorResume(e -> {
                        log.error("AI 写作流式请求异常: {}", e.getMessage(), e);
                        String errMsg = e.getMessage() != null && e.getMessage().contains("429") ?
                                "请求过于频繁，请稍后重试" : "AI 服务暂时不可用，请稍后重试";
                        return Flux.error(new BusinessException(errMsg));
                    });
        } catch (Exception e) {
            log.error("AI 写作流式请求构建失败: {}", e.getMessage(), e);
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
