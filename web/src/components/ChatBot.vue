<template>
  <div class="chatbot-container">
    <!-- 悬浮按钮 -->
    <Transition name="bounce">
      <button
        v-show="!isOpen"
        class="chatbot-trigger"
        @click="isOpen = true"
        aria-label="打开 AI 助手"
      >
        <SvgIcon name="chat" class="w-6 h-6" />
      </button>
    </Transition>

    <!-- 聊天窗口 -->
    <Transition name="slide-up">
      <div v-show="isOpen" class="chatbot-window">
        <!-- 头部 -->
        <div class="chatbot-header">
          <div class="flex items-center gap-2">
            <div class="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center">
              <span class="text-white text-sm">🤖</span>
            </div>
            <div>
              <h3 class="font-semibold text-sm m-0">Syne AI 助手</h3>
              <p class="text-xs text-white/80 m-0">有问题尽管问我~</p>
            </div>
          </div>
          <button class="close-btn" @click="isOpen = false">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </button>
        </div>

        <!-- 消息列表 -->
        <div ref="messagesContainer" class="chatbot-messages">
          <!-- 欢迎消息 -->
          <div v-if="messages.length === 0" class="welcome-message">
            <p class="text-center text-gray-500 text-sm mb-4">
              👋 你好！我是 Syne 的 AI 助手
            </p>
            <div class="quick-questions">
              <button
                v-for="q in quickQuestions"
                :key="q"
                class="quick-btn"
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
            :class="['message', msg.role]"
          >
            <div class="message-content">
              <div class="whitespace-pre-wrap">{{ msg.content }}</div>
            </div>
          </div>

          <!-- 加载中 -->
          <div v-if="loading" class="message assistant">
            <div class="message-content loading">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chatbot-input">
          <input
            v-model="inputText"
            type="text"
            placeholder="输入你的问题..."
            @keyup.enter="sendMessage()"
            :disabled="loading"
          />
          <button
            class="send-btn"
            @click="sendMessage()"
            :disabled="!inputText.trim() || loading"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 20 20" fill="currentColor">
              <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z" />
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
import SvgIcon from '@/components/Icon/SvgIcon.vue'

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
      messages.value[aiMessageIndex].content += '\n[AI 服务暂时不可用]'
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

/* 触发按钮 */
.chatbot-trigger {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  transition: transform 0.2s, box-shadow 0.2s;
}

.chatbot-trigger:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.5);
}

/* 聊天窗口 */
.chatbot-window {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 360px;
  height: 500px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 头部 */
.chatbot-header {
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
  transition: background 0.2s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 消息区域 */
.chatbot-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f8f9fa;
}

.welcome-message {
  padding: 20px 0;
}

.quick-questions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.quick-btn {
  padding: 8px 16px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  cursor: pointer;
  font-size: 13px;
  color: #4b5563;
  transition: all 0.2s;
  text-align: left;
}

.quick-btn:hover {
  background: #f3f4f6;
  border-color: #667eea;
  color: #667eea;
}

/* 消息样式 */
.message {
  margin-bottom: 12px;
  display: flex;
}

.message.user {
  justify-content: flex-end;
}

.message.assistant {
  justify-content: flex-start;
}

.message-content {
  max-width: 80%;
  padding: 10px 14px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.5;
}

.message.user .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message.assistant .message-content {
  background: white;
  color: #374151;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 加载动画 */
.message-content.loading {
  display: flex;
  gap: 4px;
  padding: 14px 18px;
}

.dot {
  width: 8px;
  height: 8px;
  background: #667eea;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.dot:nth-child(1) { animation-delay: -0.32s; }
.dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* 输入区域 */
.chatbot-input {
  padding: 12px 16px;
  background: white;
  border-top: 1px solid #e5e7eb;
  display: flex;
  gap: 8px;
}

.chatbot-input input {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 24px;
  outline: none;
  font-size: 14px;
  transition: border-color 0.2s;
}

.chatbot-input input:focus {
  border-color: #667eea;
}

.send-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity 0.2s;
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 动画 */
.bounce-enter-active {
  animation: bounce-in 0.3s;
}

.bounce-leave-active {
  animation: bounce-in 0.3s reverse;
}

@keyframes bounce-in {
  0% { transform: scale(0); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* 响应式 */
@media (max-width: 480px) {
  .chatbot-window {
    width: calc(100vw - 32px);
    height: calc(100vh - 100px);
    bottom: 70px;
    right: -8px;
  }
}
</style>
