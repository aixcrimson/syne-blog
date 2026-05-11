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
  /** 当前浏览的文章 ID（可选） */
  articleId?: number | null
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
      let buffer = ''

      function read() {
        reader?.read().then(({ done, value }) => {
          if (done) {
            if (buffer) {
              buffer += decoder.decode()
              buffer = buffer.replace(/\r\n/g, '\n')
              parseBuffer()
            }
            onDone?.()
            return
          }
          const text = decoder.decode(value, { stream: true })
          buffer += text
          buffer = buffer.replace(/\r\n/g, '\n')
          parseBuffer()
          read()
        })
      }

      function parseBuffer() {
        let delimiterIndex = buffer.indexOf('\n\n')
        while (delimiterIndex !== -1) {
          const rawEvent = buffer.slice(0, delimiterIndex)
          buffer = buffer.slice(delimiterIndex + 2)
          const lines = rawEvent.split('\n')
          const dataLines: string[] = []
          lines.forEach(line => {
            if (line.startsWith('data:')) {
              dataLines.push(line.slice(5).trimStart())
            }
          })
          if (dataLines.length > 0) {
            let content = dataLines.join('\n').replace(/\\n/g, '\n')
            // 兜底清理，防止 data: 前缀泄漏到正文
            content = content.replace(/^data:\s?/gm, '')
            onMessage(content)
          }
          delimiterIndex = buffer.indexOf('\n\n')
        }
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
