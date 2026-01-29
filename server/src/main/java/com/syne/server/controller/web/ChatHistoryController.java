package com.syne.server.controller.web;

import com.syne.server.common.Result;
import com.syne.server.model.dto.ChatSessionDTO;
import com.syne.server.model.dto.ChatSessionSyncDTO;
import com.syne.server.model.vo.ChatSessionVO;
import com.syne.server.service.ChatHistoryService;
import com.syne.server.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chat History", description = "User chat history APIs")
@RestController("webChatHistoryController")
@RequestMapping("/web/chat")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    @Operation(summary = "List chat sessions")
    @GetMapping("/sessions")
    public Result<List<ChatSessionVO>> listSessions() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(chatHistoryService.listSessions(userId));
    }

    @Operation(summary = "Get chat session")
    @GetMapping("/sessions/{id}")
    public Result<ChatSessionVO> getSession(@PathVariable String id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(chatHistoryService.getSession(userId, id));
    }

    @Operation(summary = "Create chat session")
    @PostMapping("/sessions")
    public Result<ChatSessionVO> createSession(@Valid @RequestBody ChatSessionDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(chatHistoryService.createSession(userId, dto));
    }

    @Operation(summary = "Update chat session")
    @PutMapping("/sessions/{id}")
    public Result<Void> updateSession(@PathVariable String id, @RequestBody ChatSessionDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        chatHistoryService.updateSession(userId, id, dto);
        return Result.success();
    }

    @Operation(summary = "Delete chat session")
    @DeleteMapping("/sessions/{id}")
    public Result<Void> deleteSession(@PathVariable String id) {
        Long userId = SecurityUtils.getCurrentUserId();
        chatHistoryService.deleteSession(userId, id);
        return Result.success();
    }

    @Operation(summary = "Sync chat sessions")
    @PostMapping("/sessions/sync")
    public Result<Void> syncSessions(@RequestBody ChatSessionSyncDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        chatHistoryService.syncSessions(userId, dto != null ? dto.getSessions() : null);
        return Result.success();
    }

    @Operation(summary = "Search chat sessions")
    @GetMapping("/sessions/search")
    public Result<List<ChatSessionVO>> searchSessions(@RequestParam(required = false) String keyword) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(chatHistoryService.searchSessions(userId, keyword));
    }
}
