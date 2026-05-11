package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syne.server.config.ai.AiProperties;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.ArticleMapper;
import com.syne.server.mapper.CategoryMapper;
import com.syne.server.model.entity.Article;
import com.syne.server.model.entity.Category;
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
    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;

    /**
     * 系统提示词模板
     */
    private static final String SYSTEM_PROMPT_TEMPLATE = """
            你是 Syne Blog 的 AI 助手，一个友好、专业的博客智能问答机器人。

            【博客信息】
            - 博客名称：Syne Blog
            - 技术栈：Spring Boot + Vue 3 + PostgreSQL + Spring AI
            - 功能特性：文章管理、分类标签、AI 写作助手、AI 智能问答（RAG）、评论系统、全文搜索、导航收藏
            - 联系博主：通过关于页面查看博主信息，或在文章评论区留言

            【博客统计】
            %s

            %s

            【检索到的相关文章内容】
            %s

            你的职责：
            1. 优先基于上面提供的博客内容和统计信息回答用户问题
            2. 如果用户询问文章分类、数量等统计问题，使用【博客统计】中的数据回答
            3. 如果用户正在阅读某篇文章并询问相关问题，使用【当前文章】中的内容深入分析
            4. 回答要简洁明了，使用中文，使用 Markdown 格式
            5. 当回答涉及具体文章时，提及文章标题帮助用户定位
            6. 如果没有相关信息，诚实说明并建议用户浏览博客寻找答案
            """;

    @Override
    public String chat(String question, List<Map<String, String>> history, Long articleId) {
        String systemPrompt = buildSystemPrompt(question, articleId);

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
    public Flux<String> chatStream(String question, List<Map<String, String>> history, Long articleId) {
        String systemPrompt = buildSystemPrompt(question, articleId);

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
     * 构建完整的系统提示词
     */
    private String buildSystemPrompt(String question, Long articleId) {
        String ragContext = retrieveContext(question);
        String statsInfo = buildStatsInfo();
        String articleContext = buildArticleContext(articleId);
        return String.format(SYSTEM_PROMPT_TEMPLATE, statsInfo, articleContext, ragContext);
    }

    /**
     * 构建博客统计信息
     */
    private String buildStatsInfo() {
        try {
            // 查询已发布的文章总数
            long articleCount = articleMapper.selectCount(
                    new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1));

            // 查询分类及文章数
            List<Category> categories = categoryMapper.selectList(null);
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("- 已发布文章总数：%d 篇\n", articleCount));
            sb.append("- 分类情况：\n");

            for (Category cat : categories) {
                long catCount = articleMapper.selectCount(
                        new LambdaQueryWrapper<Article>()
                                .eq(Article::getStatus, 1)
                                .eq(Article::getCategoryId, cat.getId()));
                if (catCount > 0) {
                    sb.append(String.format("  - %s：%d 篇\n", cat.getName(), catCount));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            log.warn("构建博客统计信息失败: {}", e.getMessage());
            return "- 统计信息暂时不可用";
        }
    }

    /**
     * 构建当前文章上下文（如果用户正在阅读某篇文章）
     */
    private String buildArticleContext(Long articleId) {
        if (articleId == null) {
            return "";
        }
        try {
            Article article = articleMapper.selectById(articleId);
            if (article == null) {
                return "";
            }
            // 截取文章内容前 2000 字符，避免 prompt 过长
            String content = article.getContent();
            if (content != null && content.length() > 2000) {
                content = content.substring(0, 2000) + "...(内容已截断)";
            }
            return String.format("""
                    【当前文章】用户正在阅读以下文章，如果用户的问题与这篇文章相关，请基于文章内容深入分析回答：
                    - 标题：%s
                    - 摘要：%s
                    - 正文内容：
                    %s
                    """, article.getTitle(),
                    article.getSummary() != null ? article.getSummary() : "无",
                    content != null ? content : "无");
        } catch (Exception e) {
            log.warn("获取当前文章信息失败: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 从向量数据库检索相关上下文
     */
    private String retrieveContext(String question) {
        if (!aiProperties.getRag().isEnabled()) {
            return "暂无相关博客内容";
        }

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
