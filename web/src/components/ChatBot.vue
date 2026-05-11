<template>
  <div class="chatbot-container">
    <!-- 悬浮按钮 -->
    <Transition name="bounce">
      <button
        v-show="!isOpen"
        class="group flex items-center justify-center w-14 h-14 rounded-full bg-gradient-to-br from-primary-500 to-primary-700 text-white shadow-lg shadow-primary-500/30 transition-all duration-300 hover:scale-110 hover:shadow-xl hover:shadow-primary-500/40 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 dark:focus:ring-offset-slate-900 cursor-pointer"
        @click="openChat"
        aria-label="打开 AI 助手"
      >
        <svg
          class="w-6 h-6 transition-transform duration-300 group-hover:scale-110"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
          <path d="M8 10h.01" />
          <path d="M12 10h.01" />
          <path d="M16 10h.01" />
        </svg>
      </button>
    </Transition>

    <!-- 遮罩层：点击关闭弹窗 -->
    <Transition name="fade">
      <div
        v-show="isOpen"
        class="fixed inset-0 z-[-1]"
        @click="isOpen = false"
      />
    </Transition>

    <!-- 聊天窗口 -->
    <Transition name="slide-up">
      <div
        v-show="isOpen"
        class="chatbot-window absolute bottom-0 right-0 w-[360px] h-[500px] flex flex-col overflow-hidden rounded-2xl border border-slate-200/70 bg-white/95 shadow-2xl shadow-slate-900/10 backdrop-blur-xl dark:border-slate-700/50 dark:bg-slate-900/95 dark:shadow-slate-900/50"
      >
        <!-- 头部 -->
        <div
          class="flex items-center justify-between px-4 py-3 bg-gradient-to-r from-primary-500 to-primary-600 dark:from-primary-600 dark:to-primary-700"
        >
          <div class="flex items-center gap-3">
            <!-- 历史记录按钮 -->
            <button
              class="flex items-center justify-center w-9 h-9 rounded-full bg-white/20 backdrop-blur-sm transition-colors hover:bg-white/30 cursor-pointer"
              @click="showHistory = !showHistory"
              :aria-label="showHistory ? '关闭历史记录' : '打开历史记录'"
              :title="showHistory ? '关闭历史记录' : '历史记录'"
            >
              <svg
                class="w-5 h-5 text-white"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M12 8v4l3 3" />
                <circle cx="12" cy="12" r="10" />
              </svg>
            </button>
            <div>
              <h3 class="text-sm font-semibold text-white">Syne AI 助手</h3>
              <p class="text-xs text-white/80">有问题尽管问我~</p>
            </div>
          </div>
          <div class="flex items-center gap-1">
            <!-- 新建对话 -->
            <button
              class="flex items-center justify-center w-8 h-8 rounded-full text-white/80 transition-colors hover:bg-white/20 hover:text-white focus:outline-none focus:ring-2 focus:ring-white/50 cursor-pointer"
              @click="handleNewChat"
              aria-label="新建对话"
              title="新建对话"
            >
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 5v14M5 12h14" />
              </svg>
            </button>
            <!-- 清空当前对话 -->
            <button
              v-if="chatHistoryStore.currentMessages.length > 0"
              class="flex items-center justify-center w-8 h-8 rounded-full text-white/80 transition-colors hover:bg-white/20 hover:text-white focus:outline-none focus:ring-2 focus:ring-white/50 cursor-pointer"
              @click="handleClearMessages"
              aria-label="清空当前对话"
              title="清空当前对话"
            >
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2m3 0v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6h14z" />
              </svg>
            </button>
            <!-- 关闭按钮 -->
            <button
              class="flex items-center justify-center w-8 h-8 rounded-full text-white/80 transition-colors hover:bg-white/20 hover:text-white focus:outline-none focus:ring-2 focus:ring-white/50 cursor-pointer"
              @click="isOpen = false"
              aria-label="关闭对话窗口"
            >
              <svg class="w-5 h-5" viewBox="0 0 20 20" fill="currentColor">
                <path
                  fill-rule="evenodd"
                  d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                  clip-rule="evenodd"
                />
              </svg>
            </button>
          </div>
        </div>

        <!-- 主体区域（包含历史面板和消息区） -->
        <div class="flex flex-1 overflow-hidden relative">
          <!-- 历史记录侧边栏 -->
          <Transition name="slide-left">
            <div
              v-if="showHistory"
              class="absolute inset-0 z-10 flex flex-col bg-white dark:bg-slate-900"
            >
              <!-- 搜索框 -->
              <div class="p-3 border-b border-slate-200/80 dark:border-slate-700/50">
                <div class="relative">
                  <svg
                    class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                  >
                    <circle cx="11" cy="11" r="8" />
                    <path d="M21 21l-4.35-4.35" />
                  </svg>
                  <input
                    v-model="chatHistoryStore.searchKeyword"
                    type="text"
                    placeholder="搜索历史对话..."
                    class="w-full pl-9 pr-3 py-2 text-sm bg-slate-100/80 border border-transparent rounded-lg outline-none transition-all duration-200 placeholder:text-slate-400 focus:bg-white focus:border-primary-300 focus:ring-2 focus:ring-primary-100 dark:bg-slate-800 dark:text-slate-200 dark:placeholder:text-slate-500 dark:focus:bg-slate-800 dark:focus:border-primary-500/50"
                  />
                </div>
              </div>

              <!-- 会话列表 -->
              <div class="flex-1 overflow-y-auto p-2 space-y-1">
                <div
                  v-for="session in chatHistoryStore.sortedSessions"
                  :key="session.id"
                  :class="[
                    'group flex items-center gap-2 px-3 py-2.5 rounded-lg cursor-pointer transition-all duration-200',
                    session.id === chatHistoryStore.currentSessionId
                      ? 'bg-primary-100 text-primary-700 dark:bg-primary-900/30 dark:text-primary-400'
                      : 'hover:bg-slate-100 text-slate-600 dark:text-slate-300 dark:hover:bg-slate-800'
                  ]"
                  @click="handleSelectSession(session.id)"
                >
                  <!-- 会话图标 -->
                  <svg
                    class="w-4 h-4 flex-shrink-0 opacity-60"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                  >
                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
                  </svg>

                  <!-- 会话信息 -->
                  <div class="flex-1 min-w-0">
                    <!-- 编辑模式 -->
                    <input
                      v-if="editingSessionId === session.id"
                      v-model="editingTitle"
                      type="text"
                      class="w-full px-1 py-0.5 text-sm bg-white border border-primary-300 rounded outline-none dark:bg-slate-800 dark:border-primary-500"
                      @keyup.enter="saveSessionTitle"
                      @keyup.escape="cancelEditTitle"
                      @blur="saveSessionTitle"
                      ref="editInputRef"
                    />
                    <!-- 显示模式 -->
                    <template v-else>
                      <p class="text-sm font-medium truncate">{{ session.title }}</p>
                      <p class="text-xs opacity-60 truncate">
                        {{ formatTime(session.updatedAt) }}
                      </p>
                    </template>
                  </div>

                  <!-- 操作按钮 -->
                  <div
                    v-if="editingSessionId !== session.id"
                    class="flex items-center gap-0.5 opacity-0 group-hover:opacity-100 transition-opacity"
                  >
                    <!-- 重命名 -->
                    <button
                      class="p-1 rounded hover:bg-slate-200 dark:hover:bg-slate-700 cursor-pointer"
                      @click.stop="startEditTitle(session)"
                      title="重命名"
                    >
                      <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7" />
                        <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z" />
                      </svg>
                    </button>
                    <!-- 删除 -->
                    <button
                      class="p-1 rounded hover:bg-red-100 text-red-500 dark:hover:bg-red-900/30 cursor-pointer"
                      @click.stop="handleDeleteSession(session.id)"
                      title="删除"
                    >
                      <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2m3 0v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6h14z" />
                      </svg>
                    </button>
                  </div>
                </div>

                <!-- 空状态 -->
                <div
                  v-if="chatHistoryStore.sortedSessions.length === 0"
                  class="flex flex-col items-center justify-center py-8 text-slate-400"
                >
                  <svg class="w-12 h-12 mb-2 opacity-50" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
                  </svg>
                  <p class="text-sm">{{ chatHistoryStore.searchKeyword ? '没有找到匹配的对话' : '暂无历史对话' }}</p>
                </div>
              </div>

              <!-- 底部操作栏 -->
              <div class="p-3 border-t border-slate-200/80 dark:border-slate-700/50">
                <button
                  class="w-full flex items-center justify-center gap-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-primary-500 to-primary-600 rounded-lg transition-all duration-200 hover:shadow-md hover:shadow-primary-500/30 cursor-pointer"
                  @click="handleNewChatFromHistory"
                >
                  <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M12 5v14M5 12h14" />
                  </svg>
                  新建对话
                </button>
              </div>
            </div>
          </Transition>

          <!-- 消息列表 -->
          <div
            ref="messagesContainer"
            class="flex-1 overflow-y-auto p-4 space-y-3 bg-slate-50/50 dark:bg-slate-800/30"
          >
            <!-- 欢迎消息 -->
            <div v-if="chatHistoryStore.currentMessages.length === 0" class="py-6">
              <div class="text-center mb-6">
                <div
                  class="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-gradient-to-br from-primary-100 to-primary-200 dark:from-primary-900/50 dark:to-primary-800/50 mb-4"
                >
                  <svg
                    class="w-8 h-8 text-primary-600 dark:text-primary-400"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                  >
                    <path d="M12 2a4 4 0 014 4v2a4 4 0 01-8 0V6a4 4 0 014-4z" />
                    <path d="M16 14H8a4 4 0 00-4 4v2h16v-2a4 4 0 00-4-4z" />
                  </svg>
                </div>
                <p class="text-sm text-slate-600 dark:text-slate-300">
                  你好！我是 Syne 的 AI 助手
                </p>
                <p class="text-xs text-slate-500 dark:text-slate-400 mt-1">
                  可以问我关于博客的任何问题
                </p>
              </div>
              <div class="space-y-2">
                <button
                  v-for="q in quickQuestions"
                  :key="q"
                  class="w-full px-4 py-2.5 text-left text-sm text-slate-600 bg-white rounded-xl border border-slate-200/80 transition-all duration-200 hover:border-primary-300 hover:bg-primary-50 hover:text-primary-700 dark:text-slate-300 dark:bg-slate-800/80 dark:border-slate-700/80 dark:hover:border-primary-500/50 dark:hover:bg-primary-900/20 dark:hover:text-primary-400 cursor-pointer"
                  @click="sendMessage(q)"
                >
                  {{ q }}
                </button>
              </div>
            </div>

            <!-- 消息列表 -->
            <div
              v-for="(msg, index) in chatHistoryStore.currentMessages"
              :key="index"
              :class="[
                'flex gap-2.5',
                msg.role === 'user' ? 'flex-row-reverse' : 'flex-row'
              ]"
            >
              <!-- 头像 -->
              <img
                v-if="msg.role === 'assistant'"
                :src="aiAvatar"
                alt="AI"
                class="w-8 h-8 rounded-full object-cover flex-shrink-0 mt-0.5 ring-2 ring-primary-100 dark:ring-primary-900/30"
              />
              <img
                v-else
                :src="userAvatar"
                alt="User"
                class="w-8 h-8 rounded-full object-cover flex-shrink-0 mt-0.5 ring-2 ring-slate-200 dark:ring-slate-700"
              />
              <!-- 消息气泡 -->
              <div
                :class="[
                  'min-w-0 px-4 py-2.5 rounded-2xl text-sm leading-relaxed w-fit',
                  msg.role === 'user'
                    ? 'max-w-[75%] bg-gradient-to-br from-primary-500 to-primary-600 text-white rounded-br-md'
                    : 'max-w-[calc(100%-3rem)] bg-white text-slate-700 shadow-sm border border-slate-100 rounded-bl-md dark:bg-slate-800 dark:text-slate-200 dark:border-slate-700/50'
                ]"
              >
                <div v-if="msg.role === 'assistant' && !msg.content && loading && index === chatHistoryStore.currentMessages.length - 1" class="flex items-center gap-1.5 h-6">
                  <span class="w-2 h-2 bg-primary-500 rounded-full animate-bounce" style="animation-delay: 0s" />
                  <span class="w-2 h-2 bg-primary-500 rounded-full animate-bounce" style="animation-delay: 0.15s" />
                  <span class="w-2 h-2 bg-primary-500 rounded-full animate-bounce" style="animation-delay: 0.3s" />
                </div>
                <div
                  v-else-if="msg.role === 'assistant'"
                  class="markdown-content text-sm [&>*:first-child]:mt-0 [&>*:last-child]:mb-0"
                  v-html="renderMarkdown(msg.content)"
                />
                <div v-else class="whitespace-pre-wrap">{{ msg.content }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div
          class="flex items-center gap-2 p-3 border-t border-slate-200/80 bg-white dark:border-slate-700/50 dark:bg-slate-900"
        >
          <input
            v-model="inputText"
            type="text"
            placeholder="输入你的问题..."
            class="flex-1 px-4 py-2.5 text-sm bg-slate-100/80 border border-transparent rounded-full outline-none transition-all duration-200 placeholder:text-slate-400 focus:bg-white focus:border-primary-300 focus:ring-2 focus:ring-primary-100 dark:bg-slate-800 dark:text-slate-200 dark:placeholder:text-slate-500 dark:focus:bg-slate-800 dark:focus:border-primary-500/50 dark:focus:ring-primary-900/30"
            :disabled="loading"
            @keyup.enter="sendMessage()"
          />
          <button
            class="flex items-center justify-center w-10 h-10 rounded-full bg-gradient-to-br from-primary-500 to-primary-600 text-white transition-all duration-200 hover:shadow-lg hover:shadow-primary-500/30 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:shadow-none cursor-pointer"
            :disabled="!inputText.trim() || loading"
            @click="sendMessage()"
            aria-label="发送消息"
          >
            <svg class="w-5 h-5" viewBox="0 0 20 20" fill="currentColor">
              <path
                d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z"
              />
            </svg>
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onUnmounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { aiChatStream } from '@/api/ai'
import { useChatHistoryStore } from '@/stores/chatHistory'
import { useUserStore } from '@/stores/user'
import aiIconImg from '@/assets/images/ai-icon.png'
import defaultAvatar from '@/assets/images/avatar/defalutAvatar.jpg'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

const escapeHtml = (str: string): string => {
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function (str: string, lang: string): string {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return '<pre class="hljs"><code>' +
               hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
               '</code></pre>';
      } catch (__) {}
    }
    return '<pre class="hljs"><code>' + escapeHtml(str) + '</code></pre>';
  }
})

const chatHistoryStore = useChatHistoryStore()
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()

/** 是否已登录 */
const isLoggedIn = computed(() => userStore.isLoggedIn)

/**
 * 未登录拦截：弹提示并跳转登录页，返回 false 代表已拦截。
 */
function requireLogin(): boolean {
  if (isLoggedIn.value) return true
  ElMessage.warning('请先登录后再使用 AI 助手')
  router.push({ path: '/login', query: { redirect: route.fullPath } })
  return false
}

// 头像
const aiAvatar = aiIconImg
const userAvatar = computed(() => userStore.currentUser?.avatar || defaultAvatar)

// 当前文章 ID（如果用户在文章详情页）
const currentArticleId = computed(() => {
  if (route.path.startsWith('/article/')) {
    const id = Number(route.params.id)
    return isNaN(id) ? null : id
  }
  return null
})

const isOpen = ref(false)
const loading = ref(false)
const inputText = ref('')
const messagesContainer = ref<HTMLElement>()
const showHistory = ref(false)
let abortController: AbortController | null = null

// 编辑会话标题
const editingSessionId = ref<string | null>(null)
const editingTitle = ref('')

const quickQuestions = [
  '这个博客用什么技术栈？',
  '推荐几篇热门文章',
  '怎么联系博主？'
]

// 登录态变化：仅处理「登出」——关闭面板、终止请求、复位 loading；
// 登录后不主动拉数据，等用户点击 AI 图标时再拉，避免页面刷新即发请求。
watch(isLoggedIn, (val) => {
  if (!val) {
    isOpen.value = false
    showHistory.value = false
    if (abortController) {
      abortController.abort()
      abortController = null
    }
    loading.value = false
  }
})

/**
 * 打开聊天窗口
 * - 未登录：拦截并跳转登录页
 * - 已登录：首次打开时按需拉取服务器会话；若 token 已失效，store 会自动登出，
 *   此时再次校验登录态并跳转登录
 */
async function openChat() {
  if (!requireLogin()) return
  if (!chatHistoryStore.initialized) {
    await chatHistoryStore.init()
    // init 过程中可能因 401/403 自动登出，需要二次校验
    if (!requireLogin()) return
  }
  isOpen.value = true
  nextTick(() => {
    scrollToBottom()
  })
}

/**
 * 格式化时间
 */
function formatTime(timestamp: number): string {
  const now = Date.now()
  const diff = now - timestamp
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diff < minute) return '刚刚'
  if (diff < hour) return `${Math.floor(diff / minute)} 分钟前`
  if (diff < day) return `${Math.floor(diff / hour)} 小时前`
  if (diff < 7 * day) return `${Math.floor(diff / day)} 天前`

  const date = new Date(timestamp)
  return `${date.getMonth() + 1}/${date.getDate()}`
}

/**
 * 选择会话
 */
function handleSelectSession(id: string) {
  chatHistoryStore.switchSession(id)
  showHistory.value = false
  nextTick(() => scrollToBottom())
}

/**
 * 新建对话
 */
function handleNewChat() {
  chatHistoryStore.createSession()
  showHistory.value = false
}

/**
 * 从历史面板新建对话
 */
function handleNewChatFromHistory() {
  chatHistoryStore.createSession()
  showHistory.value = false
}

/**
 * 清空当前对话消息
 */
function handleClearMessages() {
  if (abortController) {
    abortController.abort()
    abortController = null
  }
  chatHistoryStore.clearCurrentMessages()
  loading.value = false
}

/**
 * 删除会话
 */
function handleDeleteSession(id: string) {
  chatHistoryStore.deleteSession(id)
}

/**
 * 开始编辑标题
 */
function startEditTitle(session: { id: string; title: string }) {
  editingSessionId.value = session.id
  editingTitle.value = session.title
  nextTick(() => {
    const input = document.querySelector('input[type="text"]') as HTMLInputElement
    input?.focus()
    input?.select()
  })
}

/**
 * 保存标题
 */
function saveSessionTitle() {
  if (editingSessionId.value) {
    chatHistoryStore.updateSessionTitle(editingSessionId.value, editingTitle.value)
  }
  editingSessionId.value = null
  editingTitle.value = ''
}

/**
 * 取消编辑
 */
function cancelEditTitle() {
  editingSessionId.value = null
  editingTitle.value = ''
}

/**
 * Markdown 渲染
 */
function renderMarkdown(text: string): string {
  if (!text) return ''
  return md.render(text)
}

/**
 * 发送消息
 */
function sendMessage(text?: string) {
  const message = text || inputText.value.trim()
  if (!message || loading.value) return
  if (!requireLogin()) return

  // 添加用户消息
  chatHistoryStore.addMessage({ role: 'user', content: message })
  inputText.value = ''
  loading.value = true

  // 滚动到底部
  scrollToBottom()

  // 准备历史消息（最近10条）
  const history = chatHistoryStore.currentMessages.slice(-10).map(m => ({
    role: m.role,
    content: m.content
  }))

  // 添加 AI 消息占位
  chatHistoryStore.addMessage({ role: 'assistant', content: '' })

  // 调用流式 API
  abortController = aiChatStream(
    { message, history: history.slice(0, -1), articleId: currentArticleId.value },
    // onMessage
    (chunk) => {
      chatHistoryStore.appendToLastMessage(chunk)
      scrollToBottom()
    },
    // onDone
    () => {
      loading.value = false
      chatHistoryStore.saveAfterStream()
      scrollToBottom()
      abortController = null
    },
    // onError
    (error) => {
      loading.value = false
      chatHistoryStore.appendToLastMessage('\n\n*[AI 服务暂时不可用，请稍后重试]*')
      chatHistoryStore.saveAfterStream()
      console.error('AI Chat Error:', error)
      abortController = null
    }
  )
}

/**
 * 滚动到底部
 */
function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 组件卸载时取消请求
onUnmounted(() => {
  if (abortController) {
    abortController.abort()
  }
})
</script>

<style scoped>
.chatbot-container {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 1000;
}

/* 动画 */
.bounce-enter-active {
  animation: bounce-in 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.bounce-leave-active {
  animation: bounce-in 0.3s ease reverse;
}

@keyframes bounce-in {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(16px) scale(0.95);
}

/* 历史面板动画 */
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-left-enter-from,
.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-100%);
}

/* 遮罩层动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式 */
@media (max-width: 480px) {
  .chatbot-container {
    bottom: 16px;
    right: 16px;
  }

  .chatbot-window {
    width: calc(100vw - 32px) !important;
    height: calc(100vh - 120px) !important;
    max-height: 600px;
  }
}

/* 自定义滚动条 */
.chatbot-container ::-webkit-scrollbar {
  width: 6px;
}

.chatbot-container ::-webkit-scrollbar-track {
  background: transparent;
}

.chatbot-container ::-webkit-scrollbar-thumb {
  background: theme('colors.slate.300');
  border-radius: 3px;
}

.chatbot-container ::-webkit-scrollbar-thumb:hover {
  background: theme('colors.slate.400');
}

:root.dark .chatbot-container ::-webkit-scrollbar-thumb {
  background: theme('colors.slate.600');
}

:root.dark .chatbot-container ::-webkit-scrollbar-thumb:hover {
  background: theme('colors.slate.500');
}
</style>
