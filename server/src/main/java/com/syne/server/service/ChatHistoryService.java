package com.syne.server.service;

import com.syne.server.model.dto.ChatSessionDTO;
import com.syne.server.model.vo.ChatSessionVO;

import java.util.List;

public interface ChatHistoryService {

    List<ChatSessionVO> listSessions(Long userId);

    ChatSessionVO getSession(Long userId, String sessionId);

    ChatSessionVO createSession(Long userId, ChatSessionDTO dto);

    void updateSession(Long userId, String sessionId, ChatSessionDTO dto);

    void deleteSession(Long userId, String sessionId);

    void syncSessions(Long userId, List<ChatSessionDTO> sessions);

    List<ChatSessionVO> searchSessions(Long userId, String keyword);
}
