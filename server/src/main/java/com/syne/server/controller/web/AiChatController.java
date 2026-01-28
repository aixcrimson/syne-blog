package com.syne.server.controller.web;

import com.syne.server.common.Result;
import com.syne.server.model.dto.AiChatRequestDTO;
import com.syne.server.service.RagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Web 端 AI 聊天控制器 (公开接口)
 */
@Tag(name = "AI 聊天机器人", description = "博客前台智能问答接口")
@RestController
@RequestMapping("/web/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final RagService ragService;

    /**
     * AI 聊天 - 同步接口
     */
    @Operation(summary = "AI 聊天", description = "基于博客内容的智能问答")
    @PostMapping("/chat")
    public Result<String> chat(@Valid @RequestBody AiChatRequestDTO request) {
        List<Map<String, String>> history = convertHistory(request.getHistory());
        String result = ragService.chat(request.getMessage(), history);
        return Result.success(result);
    }

    /**
     * AI 聊天 - 流式接口 (SSE)
     */
    @Operation(summary = "AI 聊天(流式)", description = "流式返回聊天内容，实现打字机效果")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@Valid @RequestBody AiChatRequestDTO request) {
        List<Map<String, String>> history = convertHistory(request.getHistory());
        return ragService.chatStream(request.getMessage(), history)
                .map(content -> "data: " + content.replace("\n", "\\n") + "\n\n");
    }

    /**
     * 转换对话历史格式
     */
    private List<Map<String, String>> convertHistory(List<AiChatRequestDTO.ChatMessage> history) {
        if (history == null || history.isEmpty()) {
            return Collections.emptyList();
        }
        return history.stream()
                .map(msg -> Map.of("role", msg.getRole(), "content", msg.getContent()))
                .collect(Collectors.toList());
    }
}
