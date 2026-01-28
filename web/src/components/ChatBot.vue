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
            <div
              class="flex items-center justify-center w-9 h-9 rounded-full bg-white/20 backdrop-blur-sm"
            >
              <svg
                class="w-5 h-5 text-white"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <circle cx="12" cy="12" r="3" />
                <path d="M12 2v2m0 16v2M4.93 4.93l1.41 1.41m11.32 11.32l1.41 1.41M2 12h2m16 0h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41" />
              </svg>
            </div>
            <div>
              <h3 class="text-sm font-semibold text-white">Syne AI 助手</h3>
              <p class="text-xs text-white/80">有问题尽管问我~</p>
            </div>
          </div>
          <div class="flex items-center gap-1">
            <!-- 清空对话 -->
            <button
              v-if="messages.length > 0"
              class="flex items-center justify-center w-8 h-8 rounded-full text-white/80 transition-colors hover:bg-white/20 hover:text-white focus:outline-none focus:ring-2 focus:ring-white/50 cursor-pointer"
              @click="clearMessages"
              aria-label="清空对话"
              title="清空对话"
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

        <!-- 消息列表 -->
        <div
          ref="messagesContainer"
          class="flex-1 overflow-y-auto p-4 space-y-3 bg-slate-50/50 dark:bg-slate-800/30"
        >
          <!-- 欢迎消息 -->
          <div v-if="messages.length === 0" class="py-6">
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
            v-for="(msg, index) in messages"
            :key="index"
            :class="[
              'flex',
              msg.role === 'user' ? 'justify-end' : 'justify-start'
            ]"
          >
            <div
              :class="[
                'max-w-[85%] px-4 py-2.5 rounded-2xl text-sm leading-relaxed',
                msg.role === 'user'
                  ? 'bg-gradient-to-br from-primary-500 to-primary-600 text-white rounded-br-md'
                  : 'bg-white text-slate-700 shadow-sm border border-slate-100 rounded-bl-md dark:bg-slate-800 dark:text-slate-200 dark:border-slate-700/50'
              ]"
            >
              <div
                v-if="msg.role === 'assistant'"
                class="prose prose-sm prose-slate dark:prose-invert max-w-none"
                v-html="renderMarkdown(msg.content)"
              />
              <div v-else class="whitespace-pre-wrap">{{ msg.content }}</div>
            </div>
          </div>

          <!-- 加载中 -->
          <div v-if="loading" class="flex justify-start">
            <div
              class="flex items-center gap-1.5 px-4 py-3 bg-white rounded-2xl rounded-bl-md shadow-sm border border-slate-100 dark:bg-slate-800 dark:border-slate-700/50"
            >
              <span class="w-2 h-2 bg-primary-500 rounded-full animate-bounce" style="animation-delay: 0s" />
              <span class="w-2 h-2 bg-primary-500 rounded-full animate-bounce" style="animation-delay: 0.15s" />
              <span class="w-2 h-2 bg-primary-500 rounded-full animate-bounce" style="animation-delay: 0.3s" />
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
import { ref, nextTick, onUnmounted } from 'vue'
import { aiChatStream } from '@/api/ai'

interface Message {
  role: 'user' | 'assistant'
  content: string
}

const isOpen = ref(false)
const loading = ref(false)
const inputText = ref('')
const messages = ref<Message[]>([])
const messagesContainer = ref<HTMLElement>()
let abortController: AbortController | null = null

const quickQuestions = [
  '这个博客用什么技术栈？',
  '推荐几篇热门文章',
  '怎么联系博主？'
]

/**
 * 打开聊天窗口
 */
function openChat() {
  isOpen.value = true
  nextTick(() => {
    scrollToBottom()
  })
}

/**
 * 清空消息
 */
function clearMessages() {
  if (abortController) {
    abortController.abort()
    abortController = null
  }
  messages.value = []
  loading.value = false
}

/**
 * 简单的 Markdown 渲染
 */
function renderMarkdown(text: string): string {
  if (!text) return ''

  return text
    // 代码块
    .replace(/```(\w*)\n([\s\S]*?)```/g, '<pre class="bg-slate-100 dark:bg-slate-900 p-3 rounded-lg overflow-x-auto my-2"><code>$2</code></pre>')
    // 行内代码
    .replace(/`([^`]+)`/g, '<code class="px-1.5 py-0.5 bg-slate-100 dark:bg-slate-700 rounded text-primary-600 dark:text-primary-400 text-xs">$1</code>')
    // 粗体
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
    // 斜体
    .replace(/\*([^*]+)\*/g, '<em>$1</em>')
    // 链接
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank" class="text-primary-600 dark:text-primary-400 hover:underline">$1</a>')
    // 换行
    .replace(/\n/g, '<br>')
}

/**
 * 发送消息
 */
function sendMessage(text?: string) {
  const message = text || inputText.value.trim()
  if (!message || loading.value) return

  // 添加用户消息
  messages.value.push({ role: 'user', content: message })
  inputText.value = ''
  loading.value = true

  // 滚动到底部
  scrollToBottom()

  // 准备历史消息（最近10条）
  const history = messages.value.slice(-10).map(m => ({
    role: m.role,
    content: m.content
  }))

  // 添加 AI 消息占位
  const aiMessageIndex = messages.value.length
  messages.value.push({ role: 'assistant', content: '' })

  // 调用流式 API
  abortController = aiChatStream(
    { message, history: history.slice(0, -1) },
    // onMessage
    (chunk) => {
      messages.value[aiMessageIndex].content += chunk
      scrollToBottom()
    },
    // onDone
    () => {
      loading.value = false
      scrollToBottom()
      abortController = null
    },
    // onError
    (error) => {
      loading.value = false
      messages.value[aiMessageIndex].content += '\n\n*[AI 服务暂时不可用，请稍后重试]*'
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
