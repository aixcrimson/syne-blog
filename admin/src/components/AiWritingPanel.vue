<template>
  <div class="ai-writing-panel">
    <!-- 触发按钮 -->
    <el-dropdown trigger="click" @command="handleCommand">
      <el-button type="primary" :loading="loading">
        <el-icon class="mr-1"><MagicStick /></el-icon>
        AI 助手
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="outline">
            <el-icon><Document /></el-icon>
            生成大纲
          </el-dropdown-item>
          <el-dropdown-item command="continue">
            <el-icon><EditPen /></el-icon>
            智能续写
          </el-dropdown-item>
          <el-dropdown-item command="polish">
            <el-icon><Brush /></el-icon>
            内容润色
          </el-dropdown-item>
          <el-dropdown-item command="summary">
            <el-icon><Reading /></el-icon>
            生成摘要
          </el-dropdown-item>
          <el-dropdown-item command="title">
            <el-icon><Promotion /></el-icon>
            标题建议
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- AI 结果弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="650px"
      :close-on-click-modal="false"
      @close="handleClose"
    >
      <div class="ai-result">
        <div v-if="loading" class="loading-container">
          <el-icon class="is-loading" :size="24"><Loading /></el-icon>
          <span class="ml-2">AI 正在思考中...</span>
        </div>
        <div v-else class="result-content">
          <pre class="whitespace-pre-wrap text-sm leading-relaxed">{{ result }}</pre>
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleApply" :disabled="!result || loading">
          应用到编辑器
        </el-button>
        <el-button type="success" @click="handleCopy" :disabled="!result || loading">
          复制内容
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  MagicStick,
  Document,
  EditPen,
  Brush,
  Reading,
  Promotion,
  Loading
} from '@element-plus/icons-vue'
import { aiWritingStream, type AiWritingRequest } from '@/api/ai'

const props = defineProps<{
  /** 当前编辑器内容 */
  content: string
  /** 选中的文本 */
  selectedText?: string
}>()

const emit = defineEmits<{
  /** 应用 AI 结果到编辑器 */
  (e: 'apply', content: string): void
}>()

const loading = ref(false)
const dialogVisible = ref(false)
const result = ref('')
const currentAction = ref<AiWritingRequest['action']>('outline')
let abortController: AbortController | null = null

const actionLabels: Record<AiWritingRequest['action'], string> = {
  outline: '生成大纲',
  continue: '智能续写',
  polish: '内容润色',
  summary: '生成摘要',
  title: '标题建议'
}

const dialogTitle = computed(() => `🤖 AI ${actionLabels[currentAction.value]}`)

/**
 * 处理菜单命令
 */
function handleCommand(action: AiWritingRequest['action']) {
  currentAction.value = action

  // 获取输入内容
  let inputContent = ''

  if (action === 'polish' || action === 'continue') {
    // 润色和续写优先使用选中文本
    inputContent = props.selectedText || props.content
  } else {
    inputContent = props.content
  }

  if (!inputContent?.trim()) {
    ElMessage.warning('请先输入内容')
    return
  }

  // 开始 AI 请求
  startAIRequest(action, inputContent)
}

/**
 * 发起 AI 请求
 */
function startAIRequest(action: AiWritingRequest['action'], content: string) {
  loading.value = true
  dialogVisible.value = true
  result.value = ''

  abortController = aiWritingStream(
    { action, content },
    // onMessage - 流式内容
    (chunk) => {
      result.value += chunk
    },
    // onDone - 完成
    () => {
      loading.value = false
    },
    // onError - 错误
    (error) => {
      loading.value = false
      ElMessage.error('AI 服务暂时不可用: ' + error.message)
    }
  )
}

/**
 * 关闭弹窗时取消请求
 */
function handleClose() {
  if (abortController) {
    abortController.abort()
    abortController = null
  }
  loading.value = false
}

/**
 * 应用到编辑器
 */
function handleApply() {
  if (result.value) {
    emit('apply', result.value)
    dialogVisible.value = false
    ElMessage.success('已应用到编辑器')
  }
}

/**
 * 复制内容
 */
async function handleCopy() {
  try {
    await navigator.clipboard.writeText(result.value)
    ElMessage.success('已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败')
  }
}
</script>

<style scoped>
.ai-writing-panel {
  display: inline-block;
}

.ai-result {
  min-height: 200px;
  max-height: 400px;
  overflow-y: auto;
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: var(--el-text-color-secondary);
}

.result-content {
  padding: 16px;
  background: var(--el-fill-color-light);
  border-radius: 8px;
}

.result-content pre {
  margin: 0;
  font-family: inherit;
  word-break: break-word;
}
</style>
