package com.syne.server.controller.admin;

import com.syne.server.common.Result;
import com.syne.server.model.dto.AiWritingRequestDTO;
import com.syne.server.service.AiWritingService;
import com.syne.server.service.EmbeddingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * AI 管理控制器
 */
@Tag(name = "AI 智能体", description = "AI 写作助手和向量管理接口")
@RestController
@RequestMapping("/admin/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiWritingService aiWritingService;
    private final EmbeddingService embeddingService;

    /**
     * AI 写作助手 - 同步接口
     */
    @Operation(summary = "AI 写作", description = "支持生成大纲、续写、润色、摘要、标题等操作")
    @PostMapping("/writing")
    public Result<String> writing(@Valid @RequestBody AiWritingRequestDTO request) {
        String result = aiWritingService.generate(request.getAction(), request.getContent());
        return Result.success(result);
    }

    /**
     * AI 写作助手 - 流式接口 (SSE)
     */
    @Operation(summary = "AI 写作(流式)", description = "流式返回 AI 生成内容")
    @PostMapping(value = "/writing/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> writingStream(@Valid @RequestBody AiWritingRequestDTO request) {
        return aiWritingService.generateStream(request.getAction(), request.getContent())
                .map(content -> "data: " + content.replace("\n", "\\n") + "\n\n")
                .concatWithValues("data: [DONE]\n\n");
    }

    /**
     * 同步所有文章向量
     */
    @Operation(summary = "同步所有文章向量", description = "将所有文章内容向量化存入数据库")
    @PostMapping("/embedding/sync")
    public Result<Integer> syncAllEmbeddings() {
        int count = embeddingService.syncAllArticles();
        return Result.success(count);
    }

    /**
     * 同步单篇文章向量
     */
    @Operation(summary = "同步单篇文章向量")
    @PostMapping("/embedding/{articleId}")
    public Result<Void> syncEmbedding(@PathVariable Long articleId) {
        embeddingService.syncArticle(articleId);
        return Result.success();
    }

    /**
     * 删除文章向量
     */
    @Operation(summary = "删除文章向量")
    @DeleteMapping("/embedding/{articleId}")
    public Result<Void> deleteEmbedding(@PathVariable Long articleId) {
        embeddingService.deleteByArticleId(articleId);
        return Result.success();
    }
}
