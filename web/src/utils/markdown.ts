import MarkdownIt from 'markdown-it'
import { getHighlighter } from '@/utils/highlighter'
import 'highlight.js/styles/github-dark.css'
import mila from 'markdown-it-link-attributes'

const hljs = getHighlighter()

// 创建 Markdown 解析器实例
const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  breaks: true
})

// 配置链接在新标签页打开
md.use(mila, {
  matcher: (href: string) => !href.startsWith('#'),
  attrs: {
    target: '_blank',
    rel: 'noopener noreferrer'
  }
})

md.renderer.rules.fence = function (tokens, idx, options, env, self) {
  const token = tokens[idx];
  const lang = token.info ? token.info.trim() : '';
  let highlightedStr = md.utils.escapeHtml(token.content);

  if (lang && hljs.getLanguage(lang)) {
    try {
      highlightedStr = hljs.highlight(token.content, { language: lang, ignoreIllegals: true }).value;
    } catch (__) {
      // ignore
    }
  }

  return `<div class="code-block-wrapper">
    <div class="code-block-header">
      <div class="code-block-header-left">
        <span class="code-block-lang">${lang || 'CODE'}</span>
      </div>
      <div class="code-block-copy" title="复制代码">
        <svg class="copy-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
          <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
        </svg>
        <svg class="success-icon" style="display: none; color: #10b981;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="20 6 9 17 4 12"></polyline>
        </svg>
      </div>
    </div>
    <pre class="hljs"><code>${highlightedStr}</code></pre>
  </div>`;
};

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
    // 提取纯文本标题，过滤掉 Markdown 符号（如 **）
    const title = titleToken?.children
      ? titleToken.children
          .filter(t => t.type === 'text' || t.type === 'code_inline')
          .map(t => t.content)
          .join('')
          .trim()
      : titleToken?.content?.trim() || ''
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
