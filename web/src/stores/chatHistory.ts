import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import { useUserStore } from './user'
import { chatHistoryApi } from '@/api/chat'
import type { ChatSession, ChatMessage } from '@/types'

/** 同步到服务器的去抖延迟（毫秒） */
const SYNC_DEBOUNCE_MS = 600

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

  // ==================== 服务器同步 ====================

  /** 重置内存状态（不触碰服务器） */
  function resetState() {
    sessions.value = []
    currentSessionId.value = null
    searchKeyword.value = ''
    initialized.value = false
    if (syncTimer !== null) {
      clearTimeout(syncTimer)
      syncTimer = null
    }
  }

  /** 判断错误是否为登录失效（401/403） */
  function isAuthError(error: any): boolean {
    const status = error?.response?.status
    return status === 401 || status === 403
  }

  /** 登录失效时清掉无效 token 并复位 store */
  function handleAuthError() {
    userStore.logout()
    resetState()
  }

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
      }
    } catch (error) {
      console.error('从服务器加载失败:', error)
      if (isAuthError(error)) {
        handleAuthError()
      }
    }
  }

  /** 立即同步到服务器（仅登录用户） */
  async function syncToServer() {
    if (!userStore.isLoggedIn) return
    try {
      await chatHistoryApi.syncSessions(sessions.value)
    } catch (error) {
      console.error('同步到服务器失败:', error)
      if (isAuthError(error)) {
        handleAuthError()
      }
    }
  }

  /** 去抖持久化：未登录直接 noop，不写本地缓存 */
  let syncTimer: ReturnType<typeof setTimeout> | null = null
  function persist() {
    if (!userStore.isLoggedIn) return
    if (syncTimer !== null) {
      clearTimeout(syncTimer)
    }
    syncTimer = setTimeout(() => {
      syncTimer = null
      syncToServer()
    }, SYNC_DEBOUNCE_MS)
  }

  // ==================== 会话管理 ====================

  /**
   * 初始化
   * 仅在登录用户首次打开聊天面板时调用；未登录时不发任何请求、不读写本地缓存。
   */
  async function init() {
    if (initialized.value) return
    if (!userStore.isLoggedIn) return

    await loadFromServer()

    // 如果服务器没有任何会话，创建一个默认会话（仅内存中，等用户发消息后再持久化）
    if (sessions.value.length === 0) {
      createSessionInMemory()
    } else if (!currentSessionId.value) {
      currentSessionId.value = sessions.value[0]?.id || null
    }

    initialized.value = true
  }

  /**
   * 监听登录态变化：登出时清空内存（杜绝跨账号串数据）；登录时不主动拉，等
   * ChatBot 调 init() 拉取，避免未打开聊天窗口时也发请求。
   */
  watch(
    () => userStore.isLoggedIn,
    (isLoggedIn) => {
      if (!isLoggedIn) {
        resetState()
      }
    }
  )

  /** 创建一个仅存在于内存的新会话（不立即同步） */
  function createSessionInMemory(title?: string): ChatSession {
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
    return session
  }

  /** 创建新会话 */
  function createSession(title?: string): ChatSession {
    const session = createSessionInMemory(title)
    persist()
    return session
  }

  /** 切换会话（仅前端状态，无需同步） */
  function switchSession(id: string) {
    const session = sessions.value.find(s => s.id === id)
    if (session) {
      currentSessionId.value = id
    }
  }

  /** 更新会话标题 */
  function updateSessionTitle(id: string, title: string) {
    const session = sessions.value.find(s => s.id === id)
    if (session) {
      session.title = title.trim() || '新对话'
      session.updatedAt = Date.now()
      persist()
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
        // 没有会话了，创建一个新的（仅内存）
        createSessionInMemory()
      }
    }

    persist()
  }

  /** 清空当前会话的消息 */
  function clearCurrentMessages() {
    if (!currentSession.value) return
    currentSession.value.messages = []
    currentSession.value.updatedAt = Date.now()
    persist()
  }

  // ==================== 消息管理 ====================

  /** 添加消息到当前会话 */
  function addMessage(message: Omit<ChatMessage, 'timestamp'>) {
    if (!currentSession.value) {
      createSessionInMemory()
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

    // 用户消息立即触发去抖同步；assistant 占位消息（content 为空）不触发
    if (message.content) {
      persist()
    }
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
      persist()
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
    resetState,
    createSession,
    switchSession,
    updateSessionTitle,
    deleteSession,
    clearCurrentMessages,
    addMessage,
    updateLastMessage,
    appendToLastMessage,
    saveAfterStream,
    loadFromServer,
    syncToServer
  }
})
