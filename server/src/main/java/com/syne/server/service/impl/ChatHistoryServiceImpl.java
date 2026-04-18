package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syne.server.common.ResultCode;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.ChatMessageMapper;
import com.syne.server.mapper.ChatSessionMapper;
import com.syne.server.model.dto.ChatMessageDTO;
import com.syne.server.model.dto.ChatSessionDTO;
import com.syne.server.model.entity.ChatMessage;
import com.syne.server.model.entity.ChatSession;
import com.syne.server.model.vo.ChatMessageVO;
import com.syne.server.model.vo.ChatSessionVO;
import com.syne.server.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private static final String DEFAULT_TITLE = "New Chat";

    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;

    @Override
    public List<ChatSessionVO> listSessions(Long userId) {
        requireUser(userId);

        List<ChatSession> sessions = chatSessionMapper.selectList(
            new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getUserId, userId)
                .orderByDesc(ChatSession::getUpdatedAt)
        );

        if (sessions.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> sessionIds = sessions.stream()
            .map(ChatSession::getId)
            .filter(Objects::nonNull)
            .toList();

        List<ChatMessage> messages = sessionIds.isEmpty()
            ? Collections.emptyList()
            : chatMessageMapper.selectList(
                new LambdaQueryWrapper<ChatMessage>()
                    .eq(ChatMessage::getUserId, userId)
                    .in(ChatMessage::getSessionId, sessionIds)
                    .orderByAsc(ChatMessage::getTimestamp)
            );

        Map<String, List<ChatMessage>> messagesBySession = messages.stream()
            .collect(Collectors.groupingBy(
                ChatMessage::getSessionId,
                LinkedHashMap::new,
                Collectors.toList()
            ));

        return sessions.stream()
            .map(session -> toSessionVO(session, messagesBySession.get(session.getId())))
            .collect(Collectors.toList());
    }

    @Override
    public ChatSessionVO getSession(Long userId, String sessionId) {
        requireUser(userId);
        ChatSession session = getSessionEntity(userId, sessionId);
        List<ChatMessage> messages = chatMessageMapper.selectList(
            new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getUserId, userId)
                .eq(ChatMessage::getSessionId, sessionId)
                .orderByAsc(ChatMessage::getTimestamp)
        );
        return toSessionVO(session, messages);
    }

    @Override
    @Transactional
    public ChatSessionVO createSession(Long userId, ChatSessionDTO dto) {
        requireUser(userId);

        ChatSession session = new ChatSession();
        session.setId(StringUtils.hasText(dto.getId()) ? dto.getId() : generateSessionId());
        session.setUserId(userId);
        session.setTitle(StringUtils.hasText(dto.getTitle()) ? dto.getTitle().trim() : DEFAULT_TITLE);

        long now = System.currentTimeMillis();
        long createdAt = dto.getCreatedAt() != null ? dto.getCreatedAt() : now;
        long updatedAt = dto.getUpdatedAt() != null ? dto.getUpdatedAt() : createdAt;
        session.setCreatedAt(createdAt);
        session.setUpdatedAt(updatedAt);

        chatSessionMapper.insert(session);

        List<ChatMessage> savedMessages = saveMessages(session.getId(), userId, dto.getMessages());
        return toSessionVO(session, savedMessages);
    }

    @Override
    @Transactional
    public void updateSession(Long userId, String sessionId, ChatSessionDTO dto) {
        requireUser(userId);
        ChatSession session = getSessionEntity(userId, sessionId);

        boolean changed = false;

        if (StringUtils.hasText(dto.getTitle())) {
            session.setTitle(dto.getTitle().trim());
            changed = true;
        }

        if (dto.getUpdatedAt() != null) {
            session.setUpdatedAt(dto.getUpdatedAt());
            changed = true;
        }

        if (changed) {
            if (session.getUpdatedAt() == null) {
                session.setUpdatedAt(System.currentTimeMillis());
            }
            chatSessionMapper.updateById(session);
        }

        if (dto.getMessages() != null) {
            chatMessageMapper.delete(
                new LambdaQueryWrapper<ChatMessage>()
                    .eq(ChatMessage::getUserId, userId)
                    .eq(ChatMessage::getSessionId, sessionId)
            );
            saveMessages(sessionId, userId, dto.getMessages());
            if (dto.getUpdatedAt() == null) {
                session.setUpdatedAt(System.currentTimeMillis());
                chatSessionMapper.updateById(session);
            }
        }
    }

    @Override
    public void deleteSession(Long userId, String sessionId) {
        requireUser(userId);
        chatSessionMapper.delete(
            new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getUserId, userId)
                .eq(ChatSession::getId, sessionId)
        );
    }

    @Override
    @Transactional
    public void syncSessions(Long userId, List<ChatSessionDTO> sessions) {
        requireUser(userId);

        chatSessionMapper.delete(
            new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getUserId, userId)
        );

        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        for (ChatSessionDTO dto : sessions) {
            if (dto == null) {
                continue;
            }
            ChatSession session = new ChatSession();
            session.setId(StringUtils.hasText(dto.getId()) ? dto.getId() : generateSessionId());
            session.setUserId(userId);
            session.setTitle(StringUtils.hasText(dto.getTitle()) ? dto.getTitle().trim() : DEFAULT_TITLE);

            long now = System.currentTimeMillis();
            long createdAt = dto.getCreatedAt() != null ? dto.getCreatedAt() : now;
            long updatedAt = dto.getUpdatedAt() != null ? dto.getUpdatedAt() : createdAt;
            session.setCreatedAt(createdAt);
            session.setUpdatedAt(updatedAt);

            chatSessionMapper.insert(session);
            saveMessages(session.getId(), userId, dto.getMessages());
        }
    }

    @Override
    public List<ChatSessionVO> searchSessions(Long userId, String keyword) {
        requireUser(userId);
        if (!StringUtils.hasText(keyword)) {
            return listSessions(userId);
        }

        String lowered = keyword.trim().toLowerCase(Locale.ROOT);
        return listSessions(userId).stream()
            .filter(session -> {
                if (session.getTitle() != null && session.getTitle().toLowerCase(Locale.ROOT).contains(lowered)) {
                    return true;
                }
                if (session.getMessages() == null) {
                    return false;
                }
                return session.getMessages().stream()
                    .filter(msg -> msg != null && msg.getContent() != null)
                    .anyMatch(msg -> msg.getContent().toLowerCase(Locale.ROOT).contains(lowered));
            })
            .collect(Collectors.toList());
    }

    private ChatSession getSessionEntity(Long userId, String sessionId) {
        ChatSession session = chatSessionMapper.selectOne(
            new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getUserId, userId)
                .eq(ChatSession::getId, sessionId)
        );
        if (session == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND.getCode(), "session not found");
        }
        return session;
    }

    private List<ChatMessage> saveMessages(String sessionId, Long userId, List<ChatMessageDTO> messages) {
        if (messages == null || messages.isEmpty()) {
            return Collections.emptyList();
        }

        long base = System.currentTimeMillis();
        List<ChatMessage> entities = new ArrayList<>();

        for (int i = 0; i < messages.size(); i++) {
            ChatMessageDTO dto = messages.get(i);
            if (dto == null || !StringUtils.hasText(dto.getContent())) {
                continue;
            }

            ChatMessage message = new ChatMessage();
            message.setSessionId(sessionId);
            message.setUserId(userId);
            message.setRole(StringUtils.hasText(dto.getRole()) ? dto.getRole() : "assistant");
            message.setContent(dto.getContent());
            message.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : base + i);

            chatMessageMapper.insert(message);
            entities.add(message);
        }

        return entities;
    }

    private ChatSessionVO toSessionVO(ChatSession session, List<ChatMessage> messages) {
        ChatSessionVO vo = new ChatSessionVO();
        vo.setId(session.getId());
        vo.setTitle(session.getTitle());
        vo.setCreatedAt(session.getCreatedAt());
        vo.setUpdatedAt(session.getUpdatedAt());

        if (messages == null || messages.isEmpty()) {
            vo.setMessages(Collections.emptyList());
            return vo;
        }

        List<ChatMessageVO> messageVOs = messages.stream()
            .map(this::toMessageVO)
            .collect(Collectors.toList());
        vo.setMessages(messageVOs);
        return vo;
    }

    private ChatMessageVO toMessageVO(ChatMessage message) {
        ChatMessageVO vo = new ChatMessageVO();
        vo.setRole(message.getRole());
        vo.setContent(message.getContent());
        vo.setTimestamp(message.getTimestamp());
        return vo;
    }

    private void requireUser(Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "unauthorized");
        }
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
