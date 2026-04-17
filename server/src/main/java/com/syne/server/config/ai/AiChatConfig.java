package com.syne.server.config.ai;

import org.springframework.ai.chat.client.Request;
import org.springframework.ai.chat.client.RequestInterceptor;
import org.springframework.ai.chat.client.Response;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI Chat 配置（支持自定义 provider）
 */
@Configuration
@ConditionalOnProperty(name = "ai.enabled", havingValue = "true", matchIfMissing = true)
public class AiChatConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    /**
     * 自定义 provider（如 iflow）
     */
    @Value("${spring.ai.openai.chat.options.provider:}")
    private String provider;

    @Bean
    public ChatModel chatModel() {
        // 构建 OpenAI API
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model)
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultChatOptions(options)
                .build();
    }

    @Bean
    public ChatClient.Builder customChatClientBuilder(ChatModel chatModel) {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        
        // 如果配置了 provider，添加请求拦截器注入 header
        if (provider != null && !provider.isBlank()) {
            builder.defaultSystem(systemPrompt -> systemPrompt.text(
                    systemPrompt.text() + "\n\n[provider:" + provider + "]"
            ));
        }
        
        return builder;
    }
}