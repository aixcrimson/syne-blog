/**
 * AI 智能体 API
 */
import { post } from './request'

/**
 * AI 写作请求参数
 */
export interface AiWritingRequest {
  /** 操作类型: outline | continue | polish | summary | title */
  action: 'outline' | 'continue' | 'polish' | 'summary' | 'title'
  /** 输入内容 */
  content: string
  /** 额外上下文 */
  context?: string
}

/**
 * AI 写作 - 同步请求
 */
export function aiWriting(data: AiWritingRequest) {
  return post<string>('/admin/ai/writing', data)
}

/**
 * AI 写作 - 流式请求 (SSE)
 */
export function aiWritingStream(
  data: AiWritingRequest,
  onMessage: (content: string) => void,
  onDone?: () => void,
  onError?: (error: Error) => void
): AbortController {
  const controller = new AbortController()
  const token = localStorage.getItem('token')
  const baseUrl = import.meta.env.VITE_API_URL || '/api'

  fetch(`${baseUrl}/admin/ai/writing/stream`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    },
    body: JSON.stringify(data),
    signal: controller.signal
  })
    .then(response => {
      if (!response.ok) throw new Error('请求失败')
      const reader = response.body?.getReader()
      const decoder = new TextDecoder()

      function read() {
        reader?.read().then(({ done, value }) => {
          if (done) {
            onDone?.()
            return
          }
          const text = decoder.decode(value)
          // 解析 SSE 数据
          const lines = text.split('\n')
          lines.forEach(line => {
            // 兼容 "data: xxx" 和 "data:data: xxx" 两种格式
            let content = ''
            if (line.startsWith('data:data: ')) {
              content = line.slice(11).replace(/\\n/g, '\n')
            } else if (line.startsWith('data: ')) {
              content = line.slice(6).replace(/\\n/g, '\n')
            } else {
              return
            }
            // 检测流结束标记
            if (content === '[DONE]') {
              onDone?.()
              return
            }
            onMessage(content)
          })
          read()
        }).catch(err => {
          // 读取过程中出错也要结束 loading
          if (err.name !== 'AbortError') {
            onError?.(err)
          }
        })
      }
      read()
    })
    .catch(error => {
      if (error.name !== 'AbortError') {
        onError?.(error)
      }
    })

  return controller
}

/**
 * 同步所有文章向量
 */
export function syncAllEmbeddings() {
  return post<number>('/admin/ai/embedding/sync')
}

/**
 * 同步单篇文章向量
 */
export function syncArticleEmbedding(articleId: number) {
  return post<void>(`/admin/ai/embedding/${articleId}`)
}
