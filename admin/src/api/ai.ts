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
            if (line.startsWith('data: ')) {
              const content = line.slice(6).replace(/\\n/g, '\n')
              onMessage(content)
            }
          })
          read()
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
