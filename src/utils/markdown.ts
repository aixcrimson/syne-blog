import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

// 创建 Markdown 解析器实例
const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return `<pre class="hljs"><code>${hljs.highlight(str, { language: lang, ignoreIllegals: true }).value}</code></pre>`
      } catch (__) {
        // ignore
      }
    }
    return `<pre class="hljs"><code>${md.utils.escapeHtml(str)}</code></pre>`
  }
})

/**
 * 渲染 Markdown 文本为 HTML
 * @param content Markdown 文本
 * @returns HTML 字符串
 */
export function renderMarkdown(content: string): string {
  return md.render(content)
}

/**
 * 提取 Markdown 文本的纯文本内容
 * @param content Markdown 文本
 * @returns 纯文本
 */
export function extractPlainText(content: string): string {
  const html = md.render(content)
  const div = document.createElement('div')
  div.innerHTML = html
  return div.textContent || div.innerText || ''
}

/**
 * 获取 Markdown 文本的摘要
 * @param content Markdown 文本
 * @param maxLength 最大长度
 * @returns 摘要文本
 */
export function getMarkdownSummary(content: string, maxLength: number = 200): string {
  const plainText = extractPlainText(content)
  if (plainText.length <= maxLength) {
    return plainText
  }
  return plainText.substring(0, maxLength) + '...'
}

