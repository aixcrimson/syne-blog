import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import { useUserStore } from './user'
import { chatHistoryApi } from '@/api/chat'
import type { ChatSession, ChatMessage } from '@/types'

const STORAGE_KEY = 'syne_chat_history'
const MAX_SESSIONS = 50 // localStorage 限制

/**
 * 生成唯一 ID
 */
function generateId(): string {
  return Date.now().toString(36) + Math.random().toString(36).slice(2, 9)
}

/**
 * 根据消息内容生成会话标题
 */
function generateTitle(content: string): string {
  const maxLen = 20
  const cleaned = content.replace(/\s+/g, ' ').trim()
  return cleaned.length > maxLen ? cleaned.slice(0, maxLen) + '...' : cleaned
}

export const useChatHistoryStore = defineStore('chatHistory', () => {
  const userStore = useUserStore()

  // ==================== 状态 ====================
  const sessions = ref<ChatSession[]>([])
  const currentSessionId = ref<string | null>(null)
  const searchKeyword = ref('')
  const initialized = ref(false)

  // ==================== 计算属性 ====================

  /** 当前会话 */
  const currentSession = computed(() => {
    if (!currentSessionId.value) return null
    return sessions.value.find(s => s.id === currentSessionId.value) || null
  })

  /** 当前会话的消息列表 */
  const currentMessages = computed(() => {
    return currentSession.value?.messages || []
  })

  /** 过滤后的会话列表（搜索） */
  const filteredSessions = computed(() => {
    if (!searchKeyword.value.trim()) {
      return sessions.value
    }
    const keyword = searchKeyword.value.toLowerCase()
    return sessions.value.filter(session => {
      // 搜索标题
      if (session.title.toLowerCase().includes(keyword)) return true
      // 搜索消息内容
      return session.messages.some(msg =>
        msg.content.toLowerCase().includes(keyword)
      )
    })
  })

  /** 按更新时间排序的会话列表 */
  const sortedSessions = computed(() => {
    return [...filteredSessions.value].sort((a, b) => b.updatedAt - a.updatedAt)
  })

  // ==================== localStorage 操作 ====================

  /** 从 localStorage 加载数据 */
  function loadFromLocal() {
    try {
      const data = localStorage.getItem(STORAGE_KEY)
      if (data) {
        const parsed = JSON.parse(data)
        sessions.value = parsed.sessions || []
        currentSessionId.value = parsed.currentSessionId || null

        // 验证当前会话是否存在
        if (currentSessionId.value && !sessions.value.find(s => s.id === currentSessionId.value)) {
          currentSessionId.value = sessions.value[0]?.id || null
        }
      }
    } catch (error) {
      console.error('加载聊天历史失败:', error)
      sessions.value = []
      currentSessionId.value = null
    }
  }

  /** 保存到 localStorage */
  function saveToLocal() {
    try {
      // 限制会话数量
      const limitedSessions = sessions.value.slice(0, MAX_SESSIONS)
      const data = JSON.stringify({
        sessions: limitedSessions,
        currentSessionId: currentSessionId.value
      })
      localStorage.setItem(STORAGE_KEY, data)
    } catch (error) {
      console.error('保存聊天历史失败:', error)
      // 如果存储满了，删除最旧的会话
      if (error instanceof DOMException && error.name === 'QuotaExceededError') {
        sessions.value = sessions.value.slice(0, Math.floor(sessions.value.length / 2))
        saveToLocal()
      }
    }
  }

  // ==================== 后端 API 操作（预留） ====================

  /** 从服务器加载数据 */
  async function loadFromServer() {
    if (!userStore.isLoggedIn) return
    try {
      const data = await chatHistoryApi.getSessions()
      if (Array.isArray(data)) {
        sessions.value = data
        if (!currentSessionId.value || !sessions.value.find(s => s.id === currentSessionId.value)) {
          currentSessionId.value = sessions.value[0]?.id || null
        }
        saveToLocal()
      }
    } catch (error) {
      console.error('从服务器加载失败:', error)
    }
  }

  /** 同步到服务器 */
  async function syncToServer() {
    if (!userStore.isLoggedIn) return
    try {
      await chatHistoryApi.syncSessions(sessions.value)
    } catch (error) {
      console.error('同步到服务器失败:', error)
    }
  }

  // ==================== 会话管理 ====================

  /** 初始化 */
  function init() {
    if (initialized.value) return

    loadFromLocal()

    // 如果没有会话，创建一个默认会话
    if (sessions.value.length === 0) {
      createSession()
    } else if (!currentSessionId.value) {
      currentSessionId.value = sessions.value[0]?.id || null
    }

    initialized.value = true

    watch(
      () => userStore.isLoggedIn,
      async (isLoggedIn) => {
        if (isLoggedIn) {
          await syncToServer()
          await loadFromServer()
        } else {
          loadFromLocal()
          if (sessions.value.length > 0 && !currentSessionId.value) {
            currentSessionId.value = sessions.value[0]?.id || null
          }
        }
      },
      { immediate: true }
    )
  }

  /** 创建新会话 */
  function createSession(title?: string): ChatSession {
    const now = Date.now()
    const session: ChatSession = {
      id: generateId(),
      title: title || '新对话',
      messages: [],
      createdAt: now,
      updatedAt: now
    }

    sessions.value.unshift(session)
    currentSessionId.value = session.id
    saveToLocal()

    return session
  }

  /** 切换会话 */
  function switchSession(id: string) {
    const session = sessions.value.find(s => s.id === id)
    if (session) {
      currentSessionId.value = id
      saveToLocal()
    }
  }

  /** 更新会话标题 */
  function updateSessionTitle(id: string, title: string) {
    const session = sessions.value.find(s => s.id === id)
    if (session) {
      session.title = title.trim() || '新对话'
      session.updatedAt = Date.now()
      saveToLocal()
    }
  }

  /** 删除会话 */
  function deleteSession(id: string) {
    const index = sessions.value.findIndex(s => s.id === id)
    if (index === -1) return

    sessions.value.splice(index, 1)

    // 如果删除的是当前会话
    if (currentSessionId.value === id) {
      if (sessions.value.length > 0) {
        // 切换到相邻会话
        currentSessionId.value = sessions.value[Math.min(index, sessions.value.length - 1)]?.id || null
      } else {
        // 没有会话了，创建一个新的
        createSession()
      }
    }

    saveToLocal()
  }

  /** 清空当前会话的消息 */
  function clearCurrentMessages() {
    if (!currentSession.value) return
    currentSession.value.messages = []
    currentSession.value.updatedAt = Date.now()
    saveToLocal()
  }

  // ==================== 消息管理 ====================

  /** 添加消息到当前会话 */
  function addMessage(message: Omit<ChatMessage, 'timestamp'>) {
    if (!currentSession.value) {
      createSession()
    }

    const newMessage: ChatMessage = {
      ...message,
      timestamp: Date.now()
    }

    currentSession.value!.messages.push(newMessage)
    currentSession.value!.updatedAt = Date.now()

    // 如果是用户的第一条消息，自动生成标题
    if (message.role === 'user' && currentSession.value!.messages.length === 1) {
      currentSession.value!.title = generateTitle(message.content)
    }

    saveToLocal()
  }

  /** 更新最后一条消息（用于流式响应） */
  function updateLastMessage(content: string) {
    if (!currentSession.value || currentSession.value.messages.length === 0) return

    const lastMessage = currentSession.value.messages[currentSession.value.messages.length - 1]
    if (lastMessage.role === 'assistant') {
      lastMessage.content = content
      currentSession.value.updatedAt = Date.now()
      // 不在这里保存，等流式结束后保存
    }
  }

  /** 追加内容到最后一条消息（用于流式响应） */
  function appendToLastMessage(chunk: string) {
    if (!currentSession.value || currentSession.value.messages.length === 0) return

    const lastMessage = currentSession.value.messages[currentSession.value.messages.length - 1]
    if (lastMessage.role === 'assistant') {
      lastMessage.content += chunk
    }
  }

  /** 流式响应结束后保存 */
  function saveAfterStream() {
    if (currentSession.value) {
      currentSession.value.updatedAt = Date.now()
      saveToLocal()
    }
  }

  return {
    // 状态
    sessions,
    currentSessionId,
    searchKeyword,
    initialized,

    // 计算属性
    currentSession,
    currentMessages,
    filteredSessions,
    sortedSessions,

    // 方法
    init,
    createSession,
    switchSession,
    updateSessionTitle,
    deleteSession,
    clearCurrentMessages,
    addMessage,
    updateLastMessage,
    appendToLastMessage,
    saveAfterStream,
    loadFromLocal,
    saveToLocal,
    loadFromServer,
    syncToServer
  }
})
