/**
 * 聊天历史 API
 * 预留接口，等后端实现后启用
 */

import { get, post, put, del } from './request'
import type { ChatSession } from '@/types'

/**
 * 聊天历史 API（后端实现后启用）
 */
export const chatHistoryApi = {
  /**
   * 获取会话列表
   */
  getSessions(): Promise<ChatSession[]> {
    return get('/web/chat/sessions')
  },

  /**
   * 获取单个会话详情
   */
  getSession(id: string): Promise<ChatSession> {
    return get(`/web/chat/sessions/${id}`)
  },

  /**
   * 创建会话
   */
  createSession(session: Omit<ChatSession, 'id'>): Promise<ChatSession> {
    return post('/web/chat/sessions', session)
  },

  /**
   * 更新会话
   */
  updateSession(id: string, data: Partial<ChatSession>): Promise<void> {
    return put(`/web/chat/sessions/${id}`, data)
  },

  /**
   * 删除会话
   */
  deleteSession(id: string): Promise<void> {
    return del(`/web/chat/sessions/${id}`)
  },

  /**
   * 批量同步会话（用于本地数据同步到服务器）
   */
  syncSessions(sessions: ChatSession[]): Promise<void> {
    return post('/web/chat/sessions/sync', { sessions })
  },

  /**
   * 搜索会话
   */
  searchSessions(keyword: string): Promise<ChatSession[]> {
    return get('/web/chat/sessions/search', { keyword })
  }
}
