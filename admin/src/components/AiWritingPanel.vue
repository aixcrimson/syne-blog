<template>
  <div class="inline-flex items-center">
    <!-- 触发按钮（始终可以开启新任务） -->
    <el-dropdown trigger="click" @command="handleCommand">
      <el-button type="primary">
        <el-icon class="mr-1"><MagicStick /></el-icon>
        AI 助手
      </el-button>
      <template #dropdown>
        <el-dropdown-menu class="min-w-[180px]">
          <el-dropdown-item command="outline" class="px-4 py-2">
            <div class="flex flex-col gap-1">
              <div class="font-medium flex items-center gap-1.5 text-[var(--el-text-color-primary)]"><el-icon><Document /></el-icon> 生成大纲</div>
              <div class="text-xs text-[var(--el-text-color-secondary)] leading-tight">为您提炼文章结构</div>
            </div>
          </el-dropdown-item>
          <el-dropdown-item command="continue" class="px-4 py-2">
            <div class="flex flex-col gap-1">
              <div class="font-medium flex items-center gap-1.5 text-[var(--el-text-color-primary)]"><el-icon><EditPen /></el-icon> 智能续写</div>
              <div class="text-xs text-[var(--el-text-color-secondary)] leading-tight">顺着思路为您继续写</div>
            </div>
          </el-dropdown-item>
          <el-dropdown-item command="polish" class="px-4 py-2">
            <div class="flex flex-col gap-1">
              <div class="font-medium flex items-center gap-1.5 text-[var(--el-text-color-primary)]"><el-icon><Brush /></el-icon> 内容润色</div>
              <div class="text-xs text-[var(--el-text-color-secondary)] leading-tight">修正语法，优化表达</div>
            </div>
          </el-dropdown-item>
          <el-dropdown-item command="summary" class="px-4 py-2">
            <div class="flex flex-col gap-1">
              <div class="font-medium flex items-center gap-1.5 text-[var(--el-text-color-primary)]"><el-icon><Reading /></el-icon> 生成摘要</div>
              <div class="text-xs text-[var(--el-text-color-secondary)] leading-tight">总结文章的核心要点</div>
            </div>
          </el-dropdown-item>
          <el-dropdown-item command="title" class="px-4 py-2">
            <div class="flex flex-col gap-1">
              <div class="font-medium flex items-center gap-1.5 text-[var(--el-text-color-primary)]"><el-icon><Promotion /></el-icon> 标题建议</div>
              <div class="text-xs text-[var(--el-text-color-secondary)] leading-tight">获取吸睛的文章标题</div>
            </div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- 悬浮任务中心 -->
    <el-popover
      v-if="tasks.length > 0"
      placement="bottom-end"
      width="300"
      trigger="click"
    >
      <template #reference>
        <el-badge :is-dot="hasRunningTasks" class="inline-flex ml-3">
          <el-button type="primary" plain circle class="shadow-sm">
            <el-icon :class="{ 'is-loading': hasRunningTasks }">
              <Loading v-if="hasRunningTasks" />
              <List v-else />
            </el-icon>
          </el-button>
        </el-badge>
      </template>

      <!-- 任务面板内容 -->
      <div class="flex flex-col max-h-[400px]">
        <div class="flex justify-between items-center pb-3 border-b border-[var(--el-border-color-lighter)] mb-2">
          <span class="font-bold text-[var(--el-text-color-primary)]">任务中心 ({{ tasks.length }})</span>
          <el-button type="primary" link size="small" @click="clearFinishedTasks">
            清空已完成
          </el-button>
        </div>
        <div class="flex flex-col gap-2 overflow-y-auto pr-1">
          <div
            v-for="task in tasks"
            :key="task.id"
            class="group relative flex items-center p-2.5 rounded-lg bg-[var(--el-fill-color-light)] cursor-pointer transition-all duration-200 hover:bg-[var(--el-fill-color)] hover:shadow-sm"
            @click="openTask(task)"
          >
            <div class="w-8 h-8 rounded-full bg-[var(--el-color-primary-light-9)] text-[var(--el-color-primary)] flex items-center justify-center mr-3">
              <el-icon><component :is="actionIcons[task.action]" /></el-icon>
            </div>
            <div class="flex-1 min-w-0 flex flex-col gap-0.5">
              <div class="text-sm font-medium text-[var(--el-text-color-primary)]">{{ actionLabels[task.action] }}</div>
              <div class="text-xs text-[var(--el-text-color-secondary)]">{{ task.time }}</div>
            </div>
            <div class="mr-7">
              <el-tag size="small" :type="task.loading ? 'warning' : 'success'">
                {{ task.loading ? '生成中' : '已完成' }}
              </el-tag>
            </div>
            <!-- 删除按钮 -->
            <el-button
              class="absolute right-2 opacity-0 transition-opacity duration-200 group-hover:opacity-100"
              type="danger"
              link
              size="small"
              @click.stop="removeTask(task)"
            >
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </el-popover>

    <el-dialog
      v-for="task in tasks"
      :key="task.id"
      v-model="task.dialogVisible"
      width="650px"
      append-to-body
      :close-on-click-modal="false"
      @close="handleClose(task)"
    >
      <template #header>
        <div class="flex items-center text-lg font-semibold text-[var(--el-text-color-primary)]">
          <img src="@/assets/ai-icon.png" alt="AI Icon" class="w-6 h-6 mr-2 rounded" />
          <span>AI {{ actionLabels[task.action] }}</span>
        </div>
      </template>

      <div class="min-h-[200px] max-h-[400px] overflow-y-auto">
        <!-- 流式文本实时显示 -->
        <div v-if="task.result || task.loading" class="p-4 bg-[var(--el-fill-color-light)] rounded-lg">
          <!-- 标题建议 - 列表选择模式 -->
          <template v-if="task.action === 'title' && !task.loading">
            <div class="flex flex-col gap-2">
              <div
                v-for="(title, index) in getTitleList(task)"
                :key="index"
                class="flex items-center gap-2 py-2.5 px-3.5 rounded-md border border-[var(--el-border-color-lighter)] cursor-pointer transition-all duration-200 text-sm leading-relaxed hover:bg-[var(--el-fill-color)] hover:border-[var(--el-color-primary-light-5)]"
                :class="task.selectedTitleIndex === index ? 'bg-[var(--el-color-primary-light-9)] border-[var(--el-color-primary)] text-[var(--el-color-primary)]' : ''"
                @click="task.selectedTitleIndex = index"
              >
                <el-icon v-if="task.selectedTitleIndex === index" class="text-[var(--el-color-primary)] shrink-0"><Check /></el-icon>
                <span>{{ title }}</span>
              </div>
            </div>
          </template>
          <!-- 其他类型 - 流式文本显示 -->
          <template v-else>
            <pre class="m-0 font-inherit whitespace-pre-wrap break-words text-sm leading-relaxed">{{ task.result }}<span class="blinking-cursor" v-if="task.loading">▍</span></pre>
          </template>
        </div>

        <!-- 生成中指示器（底部） -->
        <div v-if="task.loading" class="flex items-center justify-center py-4 text-[var(--el-text-color-secondary)] text-[13px] gap-2">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>AI 正在生成中，请耐心等待...</span>
        </div>

        <!-- 空状态 -->
        <div v-if="!task.result && !task.loading" class="flex items-center justify-center h-[200px] text-[var(--el-text-color-placeholder)]">
          暂无内容
        </div>
      </div>

      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="handleDismiss(task)">
            {{ task.loading ? '后台运行' : '关闭' }}
          </el-button>
          <el-button
            v-if="task.loading"
            type="danger"
            plain
            @click="cancelTask(task)"
          >
            取消任务
          </el-button>
          
          <!-- 大纲：不显示"应用"按钮 -->
          <el-button
            v-if="task.action !== 'outline'"
            type="primary"
            @click="handleApply(task)"
            :disabled="!canApply(task)"
          >
            {{ getApplyButtonText(task.action) }}
          </el-button>
          <el-button
            type="success"
            @click="handleCopy(task)"
            :disabled="!task.result || task.loading"
          >
            复制内容
          </el-button>
        </div>
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
  Loading,
  Check,
  List,
  Close
} from '@element-plus/icons-vue'
import { aiWritingStream, type AiWritingRequest } from '@/api/ai'
import dayjs from 'dayjs'

const props = defineProps<{
  /** 当前编辑器内容 */
  content: string
  /** 选中的文本 */
  selectedText?: string
}>()

const emit = defineEmits<{
  /** 应用 AI 结果到编辑器，携带 action 类型 */
  (e: 'apply', payload: { action: string; content: string }): void
}>()

interface AiTask {
  id: string
  action: AiWritingRequest['action']
  result: string
  loading: boolean
  dialogVisible: boolean
  selectedTitleIndex: number
  abortController: AbortController | null
  time: string
}

const tasks = ref<AiTask[]>([])

const actionLabels: Record<AiWritingRequest['action'], string> = {
  outline: '生成大纲',
  continue: '智能续写',
  polish: '内容润色',
  summary: '生成摘要',
  title: '标题建议'
}

const actionIcons: Record<AiWritingRequest['action'], any> = {
  outline: Document,
  continue: EditPen,
  polish: Brush,
  summary: Reading,
  title: Promotion
}

/** 是否有正在生成的任务 */
const hasRunningTasks = computed(() => {
  return tasks.value.some(t => t.loading)
})

/** 打开指定任务的弹窗 */
function openTask(task: AiTask) {
  task.dialogVisible = true
}

/** 清空已完成的任务 */
function clearFinishedTasks() {
  const finished = tasks.value.filter(t => !t.loading)
  finished.forEach(t => removeTask(t))
  ElMessage.success('已清空完成的任务')
}

/** 从结果中按行解析并去除两端多余引号 */
function getTitleList(task: AiTask) {
  if (task.action !== 'title' || !task.result) return []
  return task.result
    .split('\n')
    // 去除序号：1. 2. - 等等
    .map(line => line.replace(/^\d+[\.\、\)\]\s]*/, ''))
    // 去除两端引号（双/单/中文引号）
    .map(line => line.replace(/^["'“‘]+|["'”’]+$/g, ''))
    .map(line => line.trim())
    .filter(line => line.length > 0)
}

/** 应用按钮的文字 */
function getApplyButtonText(action: AiWritingRequest['action']) {
  switch (action) {
    case 'summary': return '应用到摘要'
    case 'title': return '应用到标题'
    default: return '应用到编辑器'
  }
}

/** 是否可以点击应用 */
function canApply(task: AiTask) {
  if (task.loading || !task.result) return false
  if (task.action === 'title') {
    const list = getTitleList(task)
    return task.selectedTitleIndex >= 0 && task.selectedTitleIndex < list.length
  }
  return true
}

/**
 * 处理菜单命令，创建新任务
 */
function handleCommand(action: AiWritingRequest['action']) {
  let inputContent = ''

  if (action === 'polish' || action === 'continue') {
    inputContent = props.selectedText || props.content
  } else {
    inputContent = props.content
  }

  if (!inputContent?.trim()) {
    ElMessage.warning('请先输入内容')
    return
  }

  const newTask: AiTask = {
    id: Date.now().toString() + Math.random().toString(36).substring(2),
    action,
    result: '',
    loading: true,
    dialogVisible: true,
    selectedTitleIndex: -1,
    abortController: null,
    time: dayjs().format('HH:mm:ss')
  }

  tasks.value.push(newTask)
  // 关键：从数组中获取 Vue 的响应式代理对象，而不是原始的普通对象
  const reactiveTask = tasks.value[tasks.value.length - 1]
  startAIRequest(reactiveTask, inputContent)
}

/**
 * 发起 AI 请求
 */
function startAIRequest(task: AiTask, content: string) {
  task.abortController = aiWritingStream(
    { action: task.action, content },
    // onMessage - 流式内容
    (chunk) => {
      task.result += chunk
    },
    // onDone - 完成
    () => {
      task.loading = false
      task.abortController = null
    },
    // onError - 错误
    (error) => {
      task.loading = false
      task.abortController = null
      ElMessage.error(`任务[${actionLabels[task.action]}]失败: ` + error.message)
    }
  )
}

/**
 * 取消任务并清理
 */
function cancelTask(task: AiTask) {
  if (task.abortController) {
    task.abortController.abort()
    task.abortController = null
  }
  removeTask(task)
  ElMessage.warning(`已取消${actionLabels[task.action]}任务`)
}

/**
 * 关闭弹窗 - 不取消请求，仅隐藏
 */
function handleClose(task: AiTask) {
  // 弹窗会自然隐藏（dialogVisible 会变成 false）
}

/**
 * 用户主动点击按钮关闭/后台运行
 */
function handleDismiss(task: AiTask) {
  task.dialogVisible = false
}

function removeTask(task: AiTask) {
  if (task.abortController) {
    task.abortController.abort()
  }
  const idx = tasks.value.findIndex(t => t.id === task.id)
  if (idx !== -1) {
    tasks.value.splice(idx, 1)
  }
}

/**
 * 应用到编辑器
 */
function handleApply(task: AiTask) {
  if (!canApply(task)) return

  let applyContent = task.result

  // 标题模式：使用选中的标题并强制截断到 50 个字符
  if (task.action === 'title') {
    const list = getTitleList(task)
    applyContent = list[task.selectedTitleIndex]
    if (applyContent.length > 50) {
      applyContent = applyContent.substring(0, 50)
    }
  }

  emit('apply', { action: task.action, content: applyContent })
  
  const successMessage: Record<string, string> = {
    summary: '已应用到摘要',
    title: '已应用到标题',
    continue: '已追加到文章末尾',
    polish: '已应用到编辑器（覆盖内容）'
  }
  ElMessage.success(successMessage[task.action] || '已应用到编辑器')
  
  // 应用后结束任务
  task.dialogVisible = false
  removeTask(task)
}

/**
 * 格式化 Markdown 文本，确保标题和列表符号后有空格
 */
function formatMarkdown(text: string) {
  if (!text) return ''
  return text
    // 确保 # 后面有空格 (例如 ##标题 -> ## 标题)
    .replace(/^(#+)(?! )/gm, '$1 ')
    // 确保 - 后面有空格 (例如 -列表 -> - 列表)
    .replace(/^(\s*-\s*)(?! )/gm, '$1 ')
}

/**
 * 复制内容
 */
async function handleCopy(task: AiTask) {
  try {
    const formattedText = formatMarkdown(task.result)
    await navigator.clipboard.writeText(formattedText)
    ElMessage.success('已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败')
  }
}
</script>

<style scoped>
/* 仅保留无法完全用 Tailwind 类名直接平替的动画和特定样式 */
.blinking-cursor {
  font-weight: bold;
  animation: blink 1s step-end infinite;
  color: var(--el-color-primary);
  margin-left: 2px;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}
</style>
