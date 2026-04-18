package com.syne.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syne.server.mapper.ArticleEmbeddingMapper;
import com.syne.server.mapper.ArticleMapper;
import com.syne.server.model.entity.Article;
import com.syne.server.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章向量化服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.enabled", havingValue = "true", matchIfMissing = true)
public class EmbeddingServiceImpl implements EmbeddingService {

    private final VectorStore vectorStore;
    private final ArticleMapper articleMapper;
    private final ArticleEmbeddingMapper articleEmbeddingMapper;
    private final ObjectMapper objectMapper;

    /**
     * 分块大小（字符数）
     */
    private static final int CHUNK_SIZE = 500;

    /**
     * 分块重叠（字符数）
     */
    private static final int CHUNK_OVERLAP = 50;

    @Override
    @Transactional
    public void syncArticle(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            log.warn("文章不存在: {}", articleId);
            return;
        }

        // 1. 删除旧的向量
        articleEmbeddingMapper.deleteByArticleId(articleId);

        // 2. 分块并向量化
        List<Document> documents = createDocuments(article);

        // 3. 存入向量数据库
        if (!documents.isEmpty()) {
            vectorStore.add(documents);
            log.info("文章向量化完成: id={}, chunks={}", articleId, documents.size());
        }
    }

    @Override
    @Transactional
    public int syncAllArticles() {
        // 查询所有已发布的文章
        List<Article> articles = articleMapper.selectList(null);

        int count = 0;
        for (Article article : articles) {
            try {
                syncArticle(article.getId());
                count++;
            } catch (Exception e) {
                log.error("文章向量化失败: id={}, error={}", article.getId(), e.getMessage());
            }
        }

        log.info("批量向量化完成: total={}, success={}", articles.size(), count);
        return count;
    }

    @Override
    @Transactional
    public void deleteByArticleId(Long articleId) {
        articleEmbeddingMapper.deleteByArticleId(articleId);
        log.info("删除文章向量: articleId={}", articleId);
    }

    /**
     * 将文章转换为 Document 列表（分块）
     */
    private List<Document> createDocuments(Article article) {
        List<Document> documents = new ArrayList<>();

        // 组合文章内容
        String fullContent = buildFullContent(article);

        // 分块
        List<String> chunks = splitIntoChunks(fullContent);

        // 构建元数据
        Map<String, Object> baseMetadata = new HashMap<>();
        baseMetadata.put("articleId", article.getId());
        baseMetadata.put("title", article.getTitle());
        baseMetadata.put("categoryId", article.getCategoryId());

        // 创建 Document
        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Object> metadata = new HashMap<>(baseMetadata);
            metadata.put("chunkIndex", i);

            Document doc = new Document(chunks.get(i), metadata);
            documents.add(doc);
        }

        return documents;
    }

    /**
     * 构建完整内容（标题 + 摘要 + 正文）
     */
    private String buildFullContent(Article article) {
        StringBuilder sb = new StringBuilder();

        if (article.getTitle() != null) {
            sb.append("标题：").append(article.getTitle()).append("\n\n");
        }

        if (article.getSummary() != null) {
            sb.append("摘要：").append(article.getSummary()).append("\n\n");
        }

        if (article.getContent() != null) {
            sb.append("正文：\n").append(article.getContent());
        }

        return sb.toString();
    }

    /**
     * 将文本分割成块
     */
    private List<String> splitIntoChunks(String text) {
        List<String> chunks = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return chunks;
        }

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + CHUNK_SIZE, text.length());

            // 尝试在句子边界分割
            if (end < text.length()) {
                int lastPeriod = text.lastIndexOf("。", end);
                int lastNewline = text.lastIndexOf("\n", end);
                int boundary = Math.max(lastPeriod, lastNewline);

                if (boundary > start + CHUNK_SIZE / 2) {
                    end = boundary + 1;
                }
            }

            chunks.add(text.substring(start, end).trim());
            start = end - CHUNK_OVERLAP;

            if (start < 0) start = 0;
        }

        return chunks;
    }
}
