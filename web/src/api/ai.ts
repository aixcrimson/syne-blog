/**
 * AI 聊天 API
 */

/**
 * AI 聊天请求参数
 */
export interface AiChatRequest {
  /** 用户消息 */
  message: string
  /** 对话历史 */
  history?: { role: 'user' | 'assistant'; content: string }[]
}

/**
 * AI 聊天 - 流式请求 (SSE)
 */
export function aiChatStream(
  data: AiChatRequest,
  onMessage: (content: string) => void,
  onDone?: () => void,
  onError?: (error: Error) => void
): AbortController {
  const controller = new AbortController()
  const baseUrl = import.meta.env.VITE_API_URL || '/api'

  fetch(`${baseUrl}/web/ai/chat/stream`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
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
