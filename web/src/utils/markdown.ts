import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

// 创建 Markdown 解析器实例
// 创建 Markdown 解析器实例
const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  breaks: true
})

md.set({
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

export type TocItem = {
  id: string
  level: number
  title: string
}

const normalizeSlug = (value: string) => {
  return value
    .trim()
    .toLowerCase()
    .replace(/\s+/g, '-')
    .replace(/[^\w\u4e00-\u9fa5-]+/g, '')
}

const buildSlug = (title: string, slugCounts: Map<string, number>) => {
  const base = normalizeSlug(title) || 'section'
  const count = (slugCounts.get(base) || 0) + 1
  slugCounts.set(base, count)
  return count > 1 ? `${base}-${count}` : base
}

export function renderMarkdownWithToc(content: string): { html: string; toc: TocItem[] } {
  const env = {}
  const tokens = md.parse(content, env)
  const toc: TocItem[] = []
  const slugCounts = new Map<string, number>()

  for (let i = 0; i < tokens.length; i += 1) {
    const token = tokens[i]
    if (token.type !== 'heading_open') continue
    const level = Number(token.tag.slice(1))
    const titleToken = tokens[i + 1]
    const title = titleToken?.content?.trim() || ''
    const id = buildSlug(title || `section-${toc.length + 1}`, slugCounts)
    token.attrSet('id', id)
    if (title) {
      toc.push({ id, level, title })
    }
  }

  const html = md.renderer.render(tokens, md.options, env)
  return { html, toc }
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
