/**
 * 聊天历史 API
 * 预留接口，等后端实现后启用
 */

import { get, post, put, del } from './request'
import type { ChatSession } from '@/types'

/**
 * 所有聊天 API 共用的 axios 配置：
 * - skipErrorMessage：关闭全局错误弹窗，由 ChatBot/store 自行处理（如静默 + 跳转登录）
 */
const chatRequestConfig = { skipErrorMessage: true }

/**
 * 聊天历史 API
 */
export const chatHistoryApi = {
  /**
   * 获取会话列表
   */
  getSessions(): Promise<ChatSession[]> {
    return get('/web/chat/sessions', undefined, chatRequestConfig)
  },

  /**
   * 获取单个会话详情
   */
  getSession(id: string): Promise<ChatSession> {
    return get(`/web/chat/sessions/${id}`, undefined, chatRequestConfig)
  },

  /**
   * 创建会话
   */
  createSession(session: Omit<ChatSession, 'id'>): Promise<ChatSession> {
    return post('/web/chat/sessions', session, chatRequestConfig)
  },

  /**
   * 更新会话
   */
  updateSession(id: string, data: Partial<ChatSession>): Promise<void> {
    return put(`/web/chat/sessions/${id}`, data, chatRequestConfig)
  },

  /**
   * 删除会话
   */
  deleteSession(id: string): Promise<void> {
    return del(`/web/chat/sessions/${id}`, undefined, chatRequestConfig)
  },

  /**
   * 批量同步会话（用于本地数据同步到服务器）
   */
  syncSessions(sessions: ChatSession[]): Promise<void> {
    return post('/web/chat/sessions/sync', { sessions }, chatRequestConfig)
  },

  /**
   * 搜索会话
   */
  searchSessions(keyword: string): Promise<ChatSession[]> {
    return get('/web/chat/sessions/search', { keyword }, chatRequestConfig)
  }
}
